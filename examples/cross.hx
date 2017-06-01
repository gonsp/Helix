def moveToVertex(index, pos) {
    moveTo(pos)
    print("Vertex reached: "|index)
    sleep(3)
}

def cross(times) {
    [_, _, h] = getPos()
    while(times > 0) {
        moveToVertex(1, [-5, 5, h])
        moveToVertex(2, [5, -5, h])
        moveToVertex(3, [-5, -5, h])
        moveToVertex(4, [5, 5, h])
        times = times - 1
    }
}

def main() {
    takeOff(5)
    cross(2)
    moveTo([0, 0, 5])
    land()
}