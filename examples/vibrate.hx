def main() {
    takeOff(20)
    a = a()
    b = b()
    vibrate(a)
    land()
}

def vibrate(amplitude) {
    print(amplitude)
    if (amplitude <= 0.5 and amplitude >= -0.5) {
        return
    }
    up(amplitude)
    forward(10)
    vibrate(-(amplitude / 1.5))
}


def a() {
    return 20
}

def b() {
    return 50
}
