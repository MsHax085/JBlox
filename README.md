![JBlox ScreenShot](http://gyazo.com/0aededb7c543ff22cbfd70c96c68cc21.png)
## What is JBlox?
JBlox is a furthermore improved version of my previous Minecraft inspired project, Cubic3D. Cubic3D was entirely built upon Display Lists, from which I managed to get some pretty decent performance readings. I decided to abandon Cubic3D because I wanted to dig deeper into OpenGL and use the newer rendering methods, like VBO (Vertex Buffer Object), but mainly because of the poor structure of the project.

#### Multithreading
All my Minecraft inspired projects have used multithreading, from the late versions of Cubic3D to the today's version of JBlox. Everything surely can be done in one single thread, look at Minecraft! But in my case, I prefer leaving one thread (the main thread) for all the rendering, while doing calculations and preparations in another. Combining those two and sending information between these without leaving one thread to wait for the other, thus losing performance is a challenging task.

## Previous Screenshots
![JBlox ScreenShot](http://gyazo.com/c185d58390ca9ed8e41310a4a3c89856.png)
![JBlox ScreenShot](http://gyazo.com/028409222bad742bcde7f21ac83a29b6.png)
![JBlox ScreenShot](http://gyazo.com/b4140b93cbf2cb8dd6176da242f4f727.png)
![JBlox ScreenShot](http://gyazo.com/9cd163db38d9cf4753e73918c88f1af2.png)
![JBlox ScreenShot](http://gyazo.com/994cbd95ee30b8fcced8192f44f5a9e0.png)
![JBlox ScreenShot](http://gyazo.com/a4a008c3b5bfdb1ca4ebd5d2ef5e0e2b.png)
