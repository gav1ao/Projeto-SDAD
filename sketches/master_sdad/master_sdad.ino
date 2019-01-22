/**
   Notas originais do código utilizado para o uso do sensor de batimentos cardíacos, no qual esse código se baseia.
 * *****************************************************************************************************************
    >> Pulse Sensor Amped 1.2 <<
    This code is for Pulse Sensor Amped by Joel Murphy and Yury Gitman
        www.pulsesensor.com
        >>> Pulse Sensor purple wire goes to Analog Pin 0 <<<
    Pulse Sensor sample aquisition and processing happens in the background via Ticker Routine
    The following variables are automatically updated:
    Signal :    int that holds the analog signal data straight from the sensor. updated every 2mS.
    IBI  :      int that holds the time interval between beats. 2mS resolution.
    BPM  :      int that holds the heart rate value, derived every beat, from averaging previous 10 IBI values.
    QS  :       boolean that is made true whenever Pulse is found and BPM is updated. User must reset.
    Pulse :     boolean that is true when a heartbeat is sensed then false in time with pin13 LED going out.

    This code is designed with output serial data to Processing sketch "PulseSensorAmped_Processing-xx"
    The Processing sketch is a simple data visualizer.
    All the work to find the heartbeat and determine the heartrate happens in the code below.
    Pin 13 LED will blink with heartbeat.
    If you want to use pin 13 for something else, adjust the interrupt handler
    It will also fade an LED on pin fadePin with every beat. Put an LED and series resistor from fadePin to GND.
    Check here for detailed code walkthrough:
    http://pulsesensor.myshopify.com/pages/pulse-sensor-amped-arduino-v1dot1

    Code Version 1.2 by Joel Murphy & Yury Gitman  Spring 2013
    This update fixes the firstBeat and secondBeat flag usage so that realistic BPM is reported.

    Adapted for the ESP + OLED By Environment Monitor.....

      Adaptado para o projeto SDAD por Vítor Gavião
 * *****************************************************************************************************************

 **/
#include <ESP8266WiFi.h>

//Necessário para a biblioteca WiFiManager (by Tzapu)
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include <WiFiManager.h>          //https://github.com/tzapu/WiFiManager

// Biblioteca para o uso do MPU6050
#include <Wire.h>

// Bibliotecas para o uso do PULSE SENSOR (Sensor de Frequência Cardíaca)
#include <Ticker.h>
#include <SPI.h>
#include <Wire.h>

// Bibliotecas para o uso do MQTT
#include <ESP8266WiFi.h>
#include <PubSubClient.h>

// Para processamento
#include <ArduinoJson.h>

// Global
#define  NOME_REDE_AP "SDAD"

// Defines
#define TOPICO_SUBSCRIBE "teste"     //tópico MQTT de escuta
#define TOPICO_PUBLISH   "testeEnvia"

#define TOPICO_SUBSCRIBE_ATUALIZAR_PARAMETROS "atualizarParametros"

// Defines topicos
#define TOPICO_PUBLISH_FREQ_CARDIACA "atualizarFreqCardiaca"
#define TOPICO_PUBLISH_INFO_VITAL "atualizarInfoVital"

#define MQTT_USER "<user>"
#define MQTT_PASSWORD "<password>"
#define CLIENT_ID "SDAD"

#define ID_DISPOSITIVO "SDAD_1"

#define LIMITE_TEMP_BAIXA_DEFAULT 36
#define LIMITE_TEMP_ALTA_DEFAULT 38

//=======================================================================================================================
// Protótipos de funções
void configurarMQTT();
void reconectarMQTT();
void mqtt_callback(char* topic, byte* payload, unsigned int length);
void initWifi();
void reconectWiFi();
void VerificaConexoesWiFIEMQTT(void);
void triggerEnvios();
void publicarTopicoMQTT(char* topico, char* mensagem);
boolean detectarQueda();
void converterDadosMPU6050();
String converterCharParaString (char* texto);

//=======================================================================================================================
// Variáveis globais gerais
StaticJsonBuffer<300> jsonBuffer;

long tempoInicialMPU = 0;
long tempoInicialPulseSensor = 0;
boolean jaConectou = false;

// WIFI
const char* SSID = ""; // SSID / nome da rede WI-FI que deseja se conectar
const char* PASSWORD = ""; // Senha da rede WI-FI que deseja se conectar



// Variáveis dos trigguers (em ms)
long tempoFreqEnvioPulseSensor = 3000;
long tempoFreqEnvioTemperatura = 3000;

// Variáveis de medição (em ms)
long tempoEntreMedicoesPulseSensor = 1000;
long tempoEntreMedicoesTemperatura = 1000;

// Variáveis para o uso da temperatura
double limite_temp_alta = 0;
double limite_temp_baixa = 0;

//=======================================================================================================================
// Variáveis para o uso do MQTT
const char* BROKER_MQTT = "m15.cloudmqtt.com"; // ip/host do broker
int BROKER_PORT = 18995; // porta do broker

// Instâncias para o MQTT
WiFiClient espClient;
PubSubClient MQTT(espClient); // instancia o mqtt

//=======================================================================================================================
// Variáveis globais necessárias ao MPU6050

const int mpu_endr = 0x68; // Endereco I2C do MPU6050
const int sda_pin = D5; // definição do pino I2C SDA
const int scl_pin = D6; // definição do pino I2C SCL

// sensitivity scale factor respective to full scale setting provided in datasheet
const uint16_t AccelScaleFactor = 16384;
const uint16_t GyroScaleFactor = 131;

int16_t ac_x, ac_y, ac_z, temperatura, gy_x, gy_y, gy_z; // Variáveis que serão utilizadas para armazenar valores dos sensor
int16_t AccelX, AccelY, AccelZ, Temperature, GyroX, GyroY, GyroZ;
double Ax, Ay, Az, Temp, Gx, Gy, Gz;

// Variáveis para detecção de quedas
boolean fall = false; //stores if a fall has occurred
boolean trigger1=false; //stores if first trigger (lower threshold) has occurred
boolean trigger2=false; //stores if second trigger (upper threshold) has occurred
boolean trigger3=false; //stores if third trigger (orientation change) has occurred

byte trigger1count=0; //stores the counts past since trigger 1 was set true
byte trigger2count=0; //stores the counts past since trigger 2 was set true
byte trigger3count=0; //stores the counts past since trigger 3 was set true
int angleChange=0;

// Fim varíaveis MPU6050

//=======================================================================================================================
// Variáveis necessárias ao PULSE SENSOR

// The Ticker/flipper routine
Ticker flipper;

//  VARIABLES
int blinkPin = 15;                // pin to blink led at each beat
int fadePin = 12;                 // pin to do fancy classy fading blink at each beat
int fadeRate = 0;                 // used to fade LED on with PWM on fadePin


// these variables are volatile because they are used during the interrupt service routine!
volatile int BPM;                   // used to hold the pulse rate
volatile int Signal;                // holds the incoming raw data
volatile int IBI = 600;             // holds the time between beats, must be seeded!
volatile boolean Pulse = false;     // true when pulse wave is high, false when it's low
volatile boolean QS = false;        // becomes true when Arduoino finds a beat.
volatile int batimentos;

volatile int rate[10];                    // array to hold last ten IBI values
volatile unsigned long sampleCounter = 0; // used to determine pulse timing
volatile unsigned long lastBeatTime = 0;  // used to find IBI
volatile int P = 512;                     // used to find peak in pulse wave, seeded
volatile int T = 512;                     // used to find trough in pulse wave, seeded
volatile int thresh = 512;                // used to find instant moment of heart beat, seeded
volatile int amp = 100;                   // used to hold amplitude of pulse waveform, seeded
volatile boolean firstBeat = true;        // used to seed rate array so we startup with reasonable BPM
volatile boolean secondBeat = false;      // used to seed rate array so we startup with reasonable BPM

// Fim varíaveis PULSE SENSOR
//=======================================================================================================================
// Início funções PULSE SENSOR

// THIS IS THE TICKER INTERRUPT SERVICE ROUTINE.
// Ticker makes sure that we take a reading every 2 miliseconds
void ISRTr() {                        // triggered when flipper fires....
  cli();                               // disable interrupts while we do this
  Signal = analogRead(A0);              // read the Pulse Sensor
  sampleCounter += 2;                         // keep track of the time in mS with this variable
  int N = sampleCounter - lastBeatTime;       // monitor the time since the last beat to avoid noise

  //  find the peak and trough of the pulse wave
  if (Signal < thresh && N > (IBI / 5) * 3) { // avoid dichrotic noise by waiting 3/5 of last IBI
    if (Signal < T) {                       // T is the trough
      T = Signal;                         // keep track of lowest point in pulse wave
    }
  }

  if (Signal > thresh && Signal > P) {        // thresh condition helps avoid noise
    P = Signal;                             // P is the peak
  }                                        // keep track of highest point in pulse wave

  //  NOW IT'S TIME TO LOOK FOR THE HEART BEAT
  // signal surges up in value every time there is a pulse
  if (N > 250) {                                  // avoid high frequency noise
    if ( (Signal > thresh) && (Pulse == false) && (N > (IBI / 5) * 3) ) {
      Pulse = true;                               // set the Pulse flag when we think there is a pulse
      digitalWrite(blinkPin, HIGH);               // turn on pin 13 LED
      IBI = sampleCounter - lastBeatTime;         // measure time between beats in mS
      lastBeatTime = sampleCounter;               // keep track of time for next pulse

      if (secondBeat) {                      // if this is the second beat, if secondBeat == TRUE
        secondBeat = false;                  // clear secondBeat flag
        for (int i = 0; i <= 9; i++) {       // seed the running total to get a realisitic BPM at startup
          rate[i] = IBI;
        }
      }

      if (firstBeat) {                       // if it's the first time we found a beat, if firstBeat == TRUE
        firstBeat = false;                   // clear firstBeat flag
        secondBeat = true;                   // set the second beat flag
        sei();                               // enable interrupts again
        return;                              // IBI value is unreliable so discard it
      }


      // keep a running total of the last 10 IBI values
      word runningTotal = 0;                  // clear the runningTotal variable

      for (int i = 0; i <= 8; i++) {          // shift data in the rate array
        rate[i] = rate[i + 1];                // and drop the oldest IBI value
        runningTotal += rate[i];              // add up the 9 oldest IBI values
      }

      rate[9] = IBI;                          // add the latest IBI to the rate array
      runningTotal += rate[9];                // add the latest IBI to runningTotal
      runningTotal /= 10;                     // average the last 10 IBI values
      BPM = 60000 / runningTotal;             // how many beats can fit into a minute? that's BPM!
      QS = true;                              // set Quantified Self flag
      // QS FLAG IS NOT CLEARED INSIDE THIS ISR
    }
  }

  if (Signal < thresh && Pulse == true) {  // when the values are going down, the beat is over
    digitalWrite(blinkPin, LOW);           // turn off pin 13 LED
    Pulse = false;                         // reset the Pulse flag so we can do it again
    amp = P - T;                           // get amplitude of the pulse wave
    thresh = amp / 2 + T;                  // set thresh at 50% of the amplitude
    P = thresh;                            // reset these for next time
    T = thresh;
  }

  if (N > 2500) {                          // if 2.5 seconds go by without a beat
    thresh = 512;                          // set thresh default
    P = 512;                               // set P default
    T = 512;                               // set T default
    lastBeatTime = sampleCounter;          // bring the lastBeatTime up to date
    firstBeat = true;                      // set these to avoid noise
    secondBeat = false;                    // when we get the heartbeat back
  }

  sei();                                   // enable interrupts when youre done!
}// end isr

void interruptSetup() {
  // Initializes Ticker to have flipper run the ISR to sample every 2mS as per original Sketch.
  flipper.attach_ms(2, ISRTr);
}

void ledFadeToBeat() {
  fadeRate -= 15;                         //  set LED fade value
  fadeRate = constrain(fadeRate, 0, 255); //  keep LED fade value from going into negative numbers!
  analogWrite(fadePin, fadeRate);         //  fade LED
}


void sendDataToProcessing(char symbol, int data ) {
  Serial.print(symbol);                // symbol prefix tells Processing what type of data is coming
  Serial.println(data);                // the data to send culminating in a carriage return
}


// Fim funções PULSE SENSOR
//=======================================================================================================================

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  // initWifi();
  configurarWifi();
  configurarMQTT();
  configurarMPU6050();
  // configurarPulseSensor();
}

void loop() {
  // put your main code here, to run repeatedly:

  //garante funcionamento das conexões WiFi e ao broker MQTT
  VerificaConexoesWiFIEMQTT();
  
  receberInfoMPU6050();
  converterDadosMPU6050();
  // imprimirValoresMPU6050naSerial();
  detectarQueda();
  
  // getBatimentos();
  // imprimirBatimentosSerial();

  // Envio de informações
  triggerEnvios();

  //keep-alive da comunicação com broker MQTT
  MQTT.loop();
}

//=======================================================================================================================
// Funções para conexão da placa a rede

//Função: inicializa e conecta-se na rede WI-FI desejada
//Parâmetros: nenhum
//Retorno: nenhum
void initWifi()
{
  delay(10);
  Serial.println("------Conexao WI-FI------");
  Serial.print("Conectando-se na rede: ");
  Serial.println(SSID);
  Serial.println("Aguarde");

  reconectWiFi();

  if (WiFi.status() == WL_CONNECTED){
    jaConectou = true;
  }
}

//Função: reconecta-se ao WiFi
//Parâmetros: nenhum
//Retorno: nenhum
void reconectWiFi()
{
  //se já está conectado a rede WI-FI, nada é feito.
  //Caso contrário, são efetuadas tentativas de conexão
  if (WiFi.status() == WL_CONNECTED) {
    return;
  } else if(jaConectou){
    Serial.println("Conexão perdida com a internet. Tentando se reconectar.");
  }

  WiFi.begin(SSID, PASSWORD); // Conecta na rede WI-FI

  while (WiFi.status() != WL_CONNECTED)
  {
    /*if(jaConectou){
      delay(2000);
    } else {
      delay(100);
    }*/
    delay(100);
    
    Serial.print(".");
  }

  Serial.println();
  Serial.print("Conectado com sucesso na rede ");
  Serial.print(SSID);
  Serial.println("IP obtido: ");
  Serial.println(WiFi.localIP());
}

void configurarWifi() {
  // Instância do WifiManager
  WiFiManager wifiManager;
  // WiFiManagerParameter custom_text("<p>This is just a text paragraph</p>");
  // wifiManager.addParameter(&custom_text);

  // Descomente para resetar as informações de rede para caso dê problema
  wifiManager.resetSettings();

  // Callback que permite entrar no modo AP caso tente acessar uma rede salva anteriomente e falher
  wifiManager.setAPCallback(configModeCallback);

  if (!wifiManager.autoConnect(NOME_REDE_AP)) {
    String msg = "Erro ao conectar a rede. Disparando timeout para efetuar uma reconexão.";
    Serial.println(msg);
    //Reseta e tenta novamente depois do timeout
    ESP.reset();
    delay(1000);
  }
  // Caso chegue aqui, é porque está conectado.
  Serial.println("Dispositivo conectado a rede com sucesso.");
}

// Callback do WifiManager
void configModeCallback (WiFiManager *myWiFiManager) {
  Serial.println("Entrando no modo de 'Configuração'.");
  Serial.println(WiFi.softAPIP());
  //if you used auto generated SSID, print it
  Serial.println(myWiFiManager->getConfigPortalSSID());
}
//=======================================================================================================================
/*
   Definições de alguns endereços mais comuns do MPU6050
   os registros podem ser facilmente encontrados no mapa de registros do MPU6050
*/
// const int MPU_ADDR =      0x68; // definição do endereço do sensor MPU6050 (0x68)
// const int WHO_AM_I =      0x75; // registro de identificação do dispositivo
const int PWR_MGMT_1 =    0x6B; // registro de configuração do gerenciamento de energia
const int GYRO_CONFIG =   0x1B; // registro de configuração do giroscópio
const int ACCEL_CONFIG =  0x1C; // registro de configuração do acelerômetro
const int ACCEL_XOUT =    0x3B; // registro de leitura do eixo X do acelerômetro

/*
   função que escreve um dado valor em um dado registro
*/
void writeRegMPU(int reg, int val)      //aceita um registro e um valor como parâmetro
{
  Wire.beginTransmission(mpu_endr);     // inicia comunicação com endereço do MPU6050
  Wire.write(reg);                      // envia o registro com o qual se deseja trabalhar
  Wire.write(val);                      // escreve o valor no registro
  Wire.endTransmission(true);           // termina a transmissão
}


/*
    função para configurar o sleep bit
*/
void setSleepOff()
{
  writeRegMPU(PWR_MGMT_1, 0); // escreve 0 no registro de gerenciamento de energia(0x68), colocando o sensor em o modo ACTIVE
}

/* função para configurar as escalas do giroscópio
   registro da escala do giroscópio: 0x1B[4:3]
   0 é 250°/s

    FS_SEL  Full Scale Range
      0        ± 250 °/s      0b00000000
      1        ± 500 °/s      0b00001000
      2        ± 1000 °/s     0b00010000
      3        ± 2000 °/s     0b00011000
*/
void setGyroScale()
{
  writeRegMPU(GYRO_CONFIG, 0);
}

/* função para configurar as escalas do acelerômetro
   registro da escala do acelerômetro: 0x1C[4:3]
   0 é 250°/s

    AFS_SEL   Full Scale Range
      0           ± 2g            0b00000000
      1           ± 4g            0b00001000
      2           ± 8g            0b00010000
      3           ± 16g           0b00011000
*/
void setAccelScale()
{
  writeRegMPU(ACCEL_CONFIG, 0);
}

void configurarMPU6050() {
  Wire.begin(sda_pin, scl_pin);
  Wire.beginTransmission(mpu_endr);
  Wire.write(0x6B);

  //Inicializa o MPU-6050
  Wire.write(0);
  Wire.endTransmission(true);

  // setSleepOff();
  setGyroScale();
  setAccelScale();
}

void receberInfoMPU6050() {
  Wire.beginTransmission(mpu_endr);
  Wire.write(0x3B);  // Começando com o registrador 0x3B (ACCEL_XOUT_H)
  Wire.endTransmission(false);

  //Solicita os dados do sensor
  Wire.requestFrom(mpu_endr, 14);

  AccelX = (((int16_t)Wire.read() << 8) | Wire.read());
  AccelY = (((int16_t)Wire.read() << 8) | Wire.read());
  AccelZ = (((int16_t)Wire.read() << 8) | Wire.read());
  Temperature = (((int16_t)Wire.read() << 8) | Wire.read());
  GyroX = (((int16_t)Wire.read() << 8) | Wire.read());
  GyroY = (((int16_t)Wire.read() << 8) | Wire.read());
  GyroZ = (((int16_t)Wire.read() << 8) | Wire.read());

  // //Armazena o valor dos sensores nas variaveis correspondentes
  // ac_x = Wire.read() << 8 | Wire.read(); //0x3B (ACCEL_XOUT_H) & 0x3C (ACCEL_XOUT_L)
  // ac_y = Wire.read() << 8 | Wire.read(); //0x3D (ACCEL_YOUT_H) & 0x3E (ACCEL_YOUT_L)
  // ac_z = Wire.read() << 8 | Wire.read(); //0x3F (ACCEL_ZOUT_H) & 0x40 (ACCEL_ZOUT_L)
  // temperatura = Wire.read() << 8 | Wire.read(); //0x41 (TEMP_OUT_H) & 0x42 (TEMP_OUT_L)
  // gy_x = Wire.read() << 8 | Wire.read(); //0x43 (GYRO_XOUT_H) & 0x44 (GYRO_XOUT_L)
  // gy_y = Wire.read() << 8 | Wire.read(); //0x45 (GYRO_YOUT_H) & 0x46 (GYRO_YOUT_L)
  // gy_z = Wire.read() << 8 | Wire.read(); //0x47 (GYRO_ZOUT_H) & 0x48 (GYRO_ZOUT_L)


  //Mostra os valores na serial


  //Aguarda 300 ms e reinicia o processo
  delay(300);
}

void converterDadosMPU6050() {
  //divide each with their sensitivity scale factor
  Ax = (double)AccelX / AccelScaleFactor;
  Ay = (double)AccelY / AccelScaleFactor;
  Az = (double)AccelZ / AccelScaleFactor;
  Temp = (double)Temperature / 340 + 36.53; //temperature formula
  Gx = (double)GyroX / GyroScaleFactor;
  Gy = (double)GyroY / GyroScaleFactor;
  Gz = (double)GyroZ / GyroScaleFactor;
}

void imprimirValoresMPU6050naSerial() {
  Serial.print("Ax: "); Serial.print(Ax);
  Serial.print(" Ay: "); Serial.print(Ay);
  Serial.print(" Az: "); Serial.print(Az);
  Serial.print(" T: "); Serial.print(Temp);
  Serial.print(" Gx: "); Serial.print(Gx);
  Serial.print(" Gy: "); Serial.print(Gy);
  Serial.print(" Gz: "); Serial.println(Gz);

  // Serial.print("Acel. X = "); Serial.print(ac_x);
  // Serial.print(" | Y = "); Serial.print(ac_y);
  // Serial.print(" | Z = "); Serial.print(ac_z);
  // Serial.print(" | Gir. X = "); Serial.print(gy_x);
  // Serial.print(" | Y = "); Serial.print(gy_y);
  // Serial.print(" | Z = "); Serial.print(gy_z);
  // Serial.print(" | Temp = "); Serial.println(temperatura/340.00+36.53);
}

boolean detectarQueda() {
  boolean retorno = false;
  // calculating Amplitute vactor for 3 axis
  float Raw_AM = pow(pow(Ax,2)+pow(Ay,2)+pow(Az,2),0.5);
  int AM = Raw_AM * 10;  // as values are within 0 to 1, I multiplied 
                         // it by for using if else conditions 
  
  Serial.println(AM);
  //Serial.println(PM);
  //delay(500);

  if (trigger3==true){
     trigger3count++;
     Serial.println(trigger3count);
     if (trigger3count>=10){ 
        angleChange = pow(pow(Gx,2)+pow(Gy,2)+pow(Gz,2),0.5);
        //delay(10);
        Serial.println(angleChange); 
        if ((angleChange>=0) && (angleChange<=10)){ //if orientation changes remains between 0-10 degrees
            fall=true; trigger3=false; trigger3count=0;
            Serial.println(angleChange);
              }
        else{ //user regained normal orientation
           trigger3=false; trigger3count=0;
           Serial.println("TRIGGER 3 DEACTIVATED");
        }
      }
   }
  if (fall==true){ //in event of a fall detection
    Serial.println("FALL DETECTED");
    // digitalWrite(11, LOW);
    // delay(20);
    // digitalWrite(11, HIGH);
    retorno = true;
    alertarQueda();
    fall=false;
   // exit(1);
    }
  if (trigger2count>=6){ //allow 0.5s for orientation change
    trigger2=false; trigger2count=0;
    Serial.println("TRIGGER 2 DECACTIVATED");
    }
  if (trigger1count>=6){ //allow 0.5s for AM to break upper threshold
    trigger1=false; trigger1count=0;
    Serial.println("TRIGGER 1 DECACTIVATED");
    }
  if (trigger2==true){
    trigger2count++;
    //angleChange=acos(((double)x*(double)bx+(double)y*(double)by+(double)z*(double)bz)/(double)AM/(double)BM);
    angleChange = pow(pow(Gx,2)+pow(Gy,2)+pow(Gz,2),0.5); Serial.println(angleChange);
    if (angleChange>=30 && angleChange<=400){ //if orientation changes by between 80-100 degrees
      trigger3=true; trigger2=false; trigger2count=0;
      Serial.println(angleChange);
      Serial.println("TRIGGER 3 ACTIVATED");
        }
    }
  if (trigger1==true){
    trigger1count++;
    if (AM>=12){ //if AM breaks upper threshold (3g)
      trigger2=true;
      Serial.println("TRIGGER 2 ACTIVATED");
      trigger1=false; trigger1count=0;
      }
    }
  if (AM<=2 && trigger2==false){ //if AM breaks lower threshold (0.4g)
    trigger1=true;
    Serial.println("TRIGGER 1 ACTIVATED");
    }
    return retorno;
//It appears that delay is needed in order not to clog the port
  delay(100);
}
//=======================================================================================================================
// Configurar PULSE SENSOR

void configurarPulseSensor() {
  interruptSetup();
}

int getBatimentos() {
  if (QS == true) {                     // Quantified Self flag is true when arduino finds a heartbeat
    QS = false;                      // reset the Quantified Self flag for next time

    delay(10); // Aguarda 10ms
    batimentos = BPM;
    return BPM; // Retorna o BPM
  }
}

void imprimirBatimentosSerial() {
  Serial.print("BPM = ");
  Serial.println(batimentos);
}
//=======================================================================================================================
// Configurar MQTT
void configurarMQTT() {
  MQTT.setServer(BROKER_MQTT, BROKER_PORT);
  MQTT.setCallback(mqtt_callback);
}

//Função que recebe as mensagens publicadas
void mqtt_callback(char* topic, byte* payload, unsigned int length) {

  String mensagem;
  String topicoSt = String(topic);

  for (int i = 0; i < length; i++) {
    char c = (char)payload[i];
    mensagem += c;
  }
  Serial.println("Tópico => " + String(topic) + " | Valor => " + String(mensagem));

  if(topicoSt.equals(TOPICO_SUBSCRIBE_ATUALIZAR_PARAMETROS)){
    atualizarParametros(mensagem);
  }
  /*
    if (message == "1") {
    digitalWrite(D5, 1);
    } else {
    digitalWrite(D5, 0);
    }
    Serial.flush();
  */
}

void publicarTopicoMQTT(char* topico, char* mensagem) {
  Serial.println("Publish: Topico -> " + String(topico) + " - Mensagem -> " + String(mensagem));
  MQTT.publish(topico, mensagem);
}

//Função: reconecta-se ao broker MQTT (caso ainda não esteja conectado ou em caso de a conexão cair)
//        em caso de sucesso na conexão ou reconexão, o subscribe dos tópicos é refeito.
//Parâmetros: nenhum
//Retorno: nenhum
void reconectarMQTT()
{
  while (!MQTT.connected())
  {
    Serial.print("* Tentando se conectar ao Broker MQTT: ");
    Serial.println(BROKER_MQTT);
    if (MQTT.connect(CLIENT_ID, MQTT_USER, MQTT_PASSWORD))
    {
      Serial.println("Conectado com sucesso ao broker MQTT!");
      MQTT.subscribe(TOPICO_SUBSCRIBE);
      MQTT.subscribe(TOPICO_SUBSCRIBE_ATUALIZAR_PARAMETROS);
    }
    else
    {
      Serial.println("Falha ao reconectar no broker.");
      Serial.println("Havera nova tentatica de conexao em 2s");
      delay(2000);
    }
  }
}

/*
  void recconectWiFi() {
  while (WiFi.status() != WL_CONNECTED) {
    delay(100);
    Serial.print(".");
  }
  }
*/

//=======================================================================================================================
// Funções gerais

//Função: verifica o estado das conexões WiFI e ao broker MQTT.
//        Em caso de desconexão (qualquer uma das duas), a conexão
//        é refeita.
//Parâmetros: nenhum
//Retorno: nenhum
void VerificaConexoesWiFIEMQTT(void)
{
  if (!MQTT.connected()){
    reconectarMQTT(); //se não há conexão com o Broker, a conexão é refeita
  }

  // reconectWiFi(); //se não há conexão com o WiFI, a conexão é refeita
}

//=======================================================================================================================
// Triggers para envio de informações
void triggerEnvios() {
  // Envio de frequência cardíaca
  /*if ((millis() - tempoInicialPulseSensor) >= tempoFreqEnvioPulseSensor) {
    // Executar envio
    publicarTopicoMQTT(TOPICO_PUBLISH_FREQ_CARDIACA, "80");
    tempoInicialPulseSensor = millis();
  }*/

  if ((millis() - tempoInicialPulseSensor) >= tempoFreqEnvioPulseSensor){
    // imprimirBatimentosSerial();

    // String jsonBatimentos = "{\"idDispositivo\": " + String(ID_DISPOSITIVO) + ", \"batimentos\": " + String(batimentos) + "}";
//    String jsonBatimentos = "{\"idDispositivo\": " + String(ID_DISPOSITIVO) + ", \"batimentos\": teste }";
//    Serial.println(jsonBatimentos);
//    
//    int jsonTam = jsonBatimentos.length() + 1;
//    char jsonChar[jsonTam];
//
//    jsonBatimentos.toCharArray(jsonChar, jsonTam);
//    
//    publicarTopicoMQTT("batimentos", jsonChar);
//
//    tempoInicialPulseSensor = millis();
  }

  if ((millis() - tempoInicialMPU) >= tempoFreqEnvioTemperatura){
    imprimirValoresMPU6050naSerial();
    tempoInicialMPU = millis();
  }
}
//=======================================================================================================================
// Alertas
void alertarQueda() {
  String jsonQueda = "{\"idDispositivo\": \"" + String(ID_DISPOSITIVO) + "\", \"queda\": \"true\" }";
  
  int jsonTam = jsonQueda.length() + 1;
  char jsonChar[jsonTam];

  jsonQueda.toCharArray(jsonChar, jsonTam);
  publicarTopicoMQTT("quedaAlerta", jsonChar);

}

void alertarTemperatura() {
  if(limite_temp_baixa == 0){
    limite_temp_baixa = LIMITE_TEMP_BAIXA_DEFAULT;
  }

  if(limite_temp_alta == 0){
    limite_temp_alta = LIMITE_TEMP_ALTA_DEFAULT;
  }

  if ((Temp >= limite_temp_alta) || (Temp <= limite_temp_baixa)) {
    String jsonTemp = "{\"idDispositivo\": " + String(ID_DISPOSITIVO) + ", \"temperatura\": " + Temp + " }";

    int jsonTam = jsonTemp.length() + 1;
    char jsonChar[jsonTam];

    jsonTemp.toCharArray(jsonChar, jsonTam);
    publicarTopicoMQTT("temperaturaAlerta", jsonChar);
  }
}

//=======================================================================================================================
// Atualizar parametros
void atualizarParametros(String json) {

  // Converter para array char
  int jsonTam = json.length() + 1;
  char jsonChar[jsonTam];
  json.toCharArray(jsonChar, jsonTam);

  JsonObject& root = jsonBuffer.parseObject(json);

  const char* idDispositivo = root["idDispositivo"];
  String idDispSt = String(idDispositivo);
  Serial.println(idDispSt);


  if (idDispSt.equals(String(ID_DISPOSITIVO))) {
    JsonObject& tempNode = root["temperatura"];
    
    limite_temp_alta = tempNode["limite_temp_alta"];
    limite_temp_baixa = tempNode["limite_temp_baixa"];

    Serial.println("Alta" + String(limite_temp_alta));
    Serial.println("Baixa" + String(limite_temp_baixa));
  }
}
//=======================================================================================================================
// Funções úteis

// Converter char para String
String converterCharParaString (const char* texto){
  String retorno;
  int tamTexto = sizeof(texto);

  for (int i = 0; i < tamTexto; i++) {
    char c = texto[i];
    retorno += c;
  }

  return retorno;
}
