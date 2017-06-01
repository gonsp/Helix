def h(pos) {
    moveTo(pos)
    backward(100)
    forward(50)
    right(50)
    backward(50)
    forward(100)
    pos = pos + [0, 60, 0]
    down(99)
    moveTo(pos-[0, 0, 99])
    return pos
}

def e(pos) {
    moveTo(pos)
    right(50)
    left(50)
    backward(50)
    right(50)
    left(50)
    backward(50)
    right(50)
    left(50)
    forward(100)
    pos = pos + [0, 60, 0]
    down(99)
    moveTo(pos-[0, 0, 99])
    return pos
}

def l(pos) {
    moveTo(pos)
    backward(100)
    right(50)
    left(50)
    forward(100)
    pos = pos + [0, 60, 0]
    down(99)
    moveTo(pos-[0, 0, 99])
    return pos
}

def o(pos) {
    moveTo(pos)
    backward(100)
    right(50)
    forward(100)
    left(50)
    right(50)
    pos = pos + [0, 60, 0]
    down(99)
    moveTo(pos-[0, 0, 99])
    return pos
}

def hello_world(pos) {
    pos = h(pos)
    pos = e(pos)
    pos = l(pos)
    pos = l(pos)
    pos = o(pos)
}

def main() {
    takeOff(100)
    hello_world(getPos())
    land()
}