def main() {
    takeOff()
    timed_forward(10, 5)
    land()
}

def timed_forward(distance, time) {
    speed = distance / time
    forward(distance, speed)
}

