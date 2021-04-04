///////////////Door Security System////////////////////
///////////////By  Clicks and Bits////////////////////
//Subscribe to Channel for More videos and content////

#define echoPin 3 //Echo Pin of HC-SR04
#define triggerPin 2 //Trigger Pin of HC-SR04
#define buzzer 8 //Pin for Piezo Buzzer
#define bulb 9 //Pin for Bulb

long timeDuration; // Time duration of sound travel
int dist; // distace of the object

void setup() {
  pinMode(triggerPin, OUTPUT); // set Trigger Pin as output 
  pinMode(echoPin, INPUT); // set Echo pin as input
  pinMode(buzzer,OUTPUT);
  pinMode(bulb,OUTPUT);
  Serial.begin(9600); 
  digitalWrite(triggerPin, LOW);
  delay(5000);  
  digitalWrite(buzzer,HIGH);
  digitalWrite(bulb,HIGH);
    delay(100);
  digitalWrite(buzzer,LOW);
  digitalWrite(bulb,LOW);
  Serial.begin(9600); 
}
void loop() {
  digitalWrite(triggerPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(triggerPin, LOW);
  timeDuration = pulseIn(echoPin, HIGH);
  dist = timeDuration * 0.034 / 2; // Total Distance Devided by 2 (as sound travels twice for going and coming back)
  Serial.println(dist);
  delay(200);
  if(dist<=70)
  {
    digitalWrite(buzzer,HIGH);
    digitalWrite(bulb,HIGH); // Bulb will remain ON untill resetting the Arduino manually
    delay(5000);
    digitalWrite(buzzer,LOW);
  }
}
