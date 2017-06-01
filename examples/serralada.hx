def main() {
    takeOff(20)
    up(1)
    serralada(getPos() + [20, 20, 20], 10)
    land()
}

def serralada(destination, steps) {
    current = getPos()
    increment = (destination - current) / [steps, steps, steps]

    while (current != destination) {
        mountain(current + increment, 4);
        current = getPos()
    }
}

def mountain(destination, height) {
    pointy_end = (destination + getPos()) / [2, 2, 2]
    pointy_end.alt = pointy_end.alt + height
    moveTo(pointy_end)
    moveTo(destination)
}

