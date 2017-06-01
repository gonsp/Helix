def main() {
    takeOff(100)
    pos = getPos()
    print("I am at: " | pos)
    print("My height is: " | pos.alt)
    print("These are the results of two operations: " | pos + [1, 1, 1] | " and " | 23 + (pos.lat * 4.5) / 2)
    land()
}

