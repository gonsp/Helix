def main() {
    takeOff(20)
    pos_ini = getPos(4)
    pos_diff = true
    while (pos_diff) {
        forward(2)
        rotate(10)
        pos_diff = (pos_ini != getPos())
        print(pos_diff);
    }
    land();

}

