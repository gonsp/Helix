def cross(times) {
    [_, _, h] = getPos()
    while(times > 0) {
        moveTo([-5, 5, h])
        sleep(3)
        moveTo([5, -5, h])
        sleep(3)
        moveTo([-5, -5, h])
        sleep(3)
        moveTo([5, 5, h])
        sleep(3)
        times = times - 1
    }
}

def main() {
    takeOff(5)
    cross(2)
    moveTo([0, 0, 0])
    land()
}