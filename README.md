# Quantum Field Simulator

## Introduction
This source code simulates the quantum field theory.
Inititially it was developed to simulate the double slit experiment.
It uses a simple motion formula to simulate the light wave.
By defining the slits in the simulator, the interference patterns can be simulated.
Even walls / mirrors can be defined freely in 3D quantum field.
There are also other files to simulate, for example rain drops or detecting the speed of light.
Please note that the simulation does not approximate real world values, but rather simulates light wave properties due to lack of cpu power.

I am not claiming that this is THE quantum field theory, but it should only be used for educational purposes to understand the concept of quantum field theory.

<p>The game library engine LWJGL is used to draw the field in 3D.</p>

### Sample files:
* double_slit.qfs
* rain.qfs
* test_speed_of_light.gfs

### Compilation
* IDE: Eclipse
* Compiler: Java 1.8

<p>Download the project to any folder. Then from File menu in Eclipse "open Projects from File System..." and import the downloaded project folder.</p>
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

### Formulas used
<img src="https://raw.githubusercontent.com/dqnguyen59/QuantumFieldSimulator/main/images/qfs_formula.png">


<p>
  I am not a physicist and do not understand the formulas used as shown in <a href="https://en.wikipedia.org/wiki/Quantum_field_theory" target="blank">Wikipedia: Quantum field theory</a>.
  But found that this formula works well for simulating double slit experiments.
</p>
<p>
  The equation of Quantum Field is simply based on the motion formula of basic physics and no special formulas are used here.
  Have a look at the QFSNode.java source code to see how this formula is derived and used in the code.
  Note that this is not the full quantum field formula, only the electric field is simulated.
  Note that v is not the speed of light, but is actually the speed of the node itself.
  The propagation of the light wave is caused by the motion of the node and the variable k determines the speed of light.
</p>

<p>
  Enjoy 😃
</p>

<p>
  Todo: add link to youtube videos and short tutorials
</p>
