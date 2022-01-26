import os
import platform
import time
import sys

time.sleep(3)

if platform.system() == "Linux":
    os.system("sudo screen -L -S wolfbot-2021.1.2-SNAPSHOT sudo java -jar wolfbot")

sys.exit(0)