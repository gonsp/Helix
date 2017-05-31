def step(radius, height) {
    [_, _, dist] = height - getPos()
    up(dist)
    forward(radius/2)
    i = 4
    while(i > 0) {
        rotate(-90)
        forward(radius)
        i = i-1
    }
    rotate(-90)
    forward(radius/2)
}

def horizontal_spiral(radius) {
    i = 0
    while(i < 10) {
        step(2*i+radius, i+10)
        i = i+1
    }
}

def main() {
    takeOff(5)
    horizontal_spiral(5)
    land()
}
