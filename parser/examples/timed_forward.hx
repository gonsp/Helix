def main() {
    if (not take_off()) {
        return
    }
    timed_forward(10, 5)
}

def timed_forward(distance, time) {
    speed = distance / time
    forward(distance, speed)
}

