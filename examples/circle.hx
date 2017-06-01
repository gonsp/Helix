def main() {
    takeOff(20)
    rotate(-50)
    forward(10)
    pos_ini = getPos()
    pos_diff = true
    while (pos_diff) {
        forward(2)
        rotate(10)
        pos_diff = (pos_ini != getPos())
    }
    land();
}

