const int ledPin = 13;
const int switchPin = 2;
const int piezoPin = 8;

int switchVal;
int current_loop = 0;
int index = 0;
boolean playing = false;


/*
This program is able to play simple songs by having a piezo play sounds of certain pitches and durations followed by pauses.
The program can also be ordered to repeat certain segments of this sequence multiple times.
It's possible to code a song by hand, but it's recommended to use the provided PiezoParser program to generate the required values below.
*/


// pre-generated code from PiezoParser goes here
// Sample: intro to song The Robots by Kraftwerk
int length = 25;
int loop_count = 4;
int pitch[] = {147, 147, 131, 131, 147, 147, 175, 175, 147, 147, 131, 131, 147, 147, 147, 147, 147, 220, 220, 262, 220, 698, 784, 1046, 880};
int duration[] = {80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 150, 150, 150, 250, 250, 150, 150, 250};
int pause[] = {50, 50, 50, 50, 50, 250, 50, 250, 50, 50, 50, 50, 50, 50, 50, 50, 250, 500, 70, 70, 400, 200, 100, 100, 1000};
int loop_begin[] = {0, 8, 0, 0};
int loop_end[] = {3, 11, 16, 24};
int loop_repeat[] = {1, 1, 1, 0};
int repeats[] = {1, 1, 1, 0};
// end of pre-generated code


void setup() {
  //Serial.begin(9600);
  pinMode(ledPin, OUTPUT);
}

void loop() {
  if (playing && index < length) {
    tone(piezoPin,pitch[index],duration[index]);
    delay(duration[index]);
    delay(pause[index]);
    if (index==loop_end[current_loop]) {
      if (repeats[current_loop]-- > 0) {
        index = loop_begin[current_loop];
        while (current_loop>0 && index<=loop_begin[current_loop-1]) {
          current_loop--;
          repeats[current_loop] = loop_repeat[current_loop];
        }        
        Serial.print("looping back to ");
        Serial.println(index);
      } else {
        current_loop++;
        index++;
      }
    }
    else {
      index++;
    }
  } else {
    playing = false;
  }
  switchVal = digitalRead(switchPin);  
  if (switchVal == HIGH) {
    reset();
    delay(100);
  }

}
void reset() {
  index=0;
  current_loop = 0;
  for (int i = 0; i < loop_count; i++) {
    repeats[i] = loop_repeat[i];
  }
  playing = true;
}
