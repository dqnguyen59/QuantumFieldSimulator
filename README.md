# Quantum Field Simulator

## Introduction
Quantum Field Simulator is a simulator to simulate quantum field theory.
Inititially it was developed to simulate the double slit experiment.
It uses a simple motion formula to simulate the light wave.
By defining the slits in the simulator, the interference patterns can be simulated.
There are other files to simulate, for example rain drops or detecting the speed of light.
Please note that the simulation does not approximate real world values, but rather simulates light wave properties due to lack of cpu power.

I am not claiming that this is THE quantum field theory, but it should only be used for educational purposes to understand the concept of quantum field theory.

<p>The game library engine LWJGL is used to draw the field in 3D.</p>

### Sample files:
* double_slit.qfs
* rain.qfs
* testSpeedOfLight.gfs

### Compilation
* IDE: Eclipse
* Compiler: Java 1.8

<p>Download the project to any folder. Then in Eclipse, File -> open Projects from File System... and select the downloaded project folder.</p>
<p>Find the file Main.java and run.</p>
<p>Make sure you have Java 8 installed. JDK 8 or later is recommended.</p>

### CPU & GPU Performance for comparison
* OS: Ubuntu 20.04.5 LTS
* Processor: AMD® Ryzen 9 3950x 16-core processor × 32 
* Graphics: AMD® Radeon rx 6800

| Nodes    | Visible Nodes | Width | Height | Depth | FPS |
|----------|---------------|-------|--------|-------|-----|
| 80631    | 6561          | 51    | 51     | 31    | 70  |
| 78961    | 78961         | 281   | 281    | 1     | 10  |

### Double-Slit example
<img src="https://raw.githubusercontent.com/dqnguyen59/QuantumFieldSimulator/main/images/double_slit.png">

### Formula used
<img src="https://raw.githubusercontent.com/dqnguyen59/QuantumFieldSimulator/main/images/qfs_formula.png">
