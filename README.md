# QuantumFieldSimulator

## Introduction
Quantum Field Simulator is a cross-platform simulator to simulate the quantum field theory. Inititially it was developed to simulate the double slit experiment. It uses simple motion formula to simulate the light wave. By defining the slits in the simulator, the interference patterns can be simulated. There are other files to simulate i.e. rain or testing speed of light.

Please note that the simulation does not approach the real world values, but rather to simulate the light wave properties due to lack of cpu power.

I am not claiming that this is THE quantum field theory, but it should only be used for educational purposes to understand the concept of quantum field theory.

<p>The game library engine LWJGL is used to draw the field in 3D.</p>

### Sample files:
* double_slit.qfs
* rain.qfs
* testSpeedOfLight.gfs

### Compilation
* IDE: Eclipse
* Compiler: Java 1.8

<p>In Eclipse, File -> open Projects from File System... and select the project folder.</p>
<p>Run Main.java</p>
<p>Make sure you have Java 8 installed. JDK 8 or later is recommended.</p>

### CPU & GPU Performance for comparison
The simulation is tested on AMD Processor 3950 hyperthreading 32 cores and AMD GPU 6800.
| Nodes    | Visible Nodes | Width | Height | Depth | FPS |
| 80631    | 6561          | 51    | 51     | 31    | 70  |
| 78961    | 78961         | 281   | 281    | 1     | 10  |

