# Windows Service Controller  

I made this out of boredom  

## Features  
- Start/Stop/Restart Windows services   

## Prerequisites  
- Windows OS  
- Java Runtime Environment (JRE) 8+  
- Administrator privileges (to control services)  

## Usage  
1. Clone/download this repository  
2. Double-click `App.java` to run (or compile manually)  
3. The app will show:  
   - Service names on the left  
   - Status indicators (●)  
   - Control buttons (Start/Stop/Restart)  

### Default Services  
- (Add your own services by editing the `SERVICE_NAMES` array)  

## How to Add More Services  
Edit the code at line:  
```java
private static final String[] SERVICE_NAMES = {
        "FirstService",    // ← Add your services here
        "AnotherService",  // ← Add here
        "ThirdService"     // ← And here
};
