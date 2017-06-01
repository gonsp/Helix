def main() {
    takeOff(20)
    vibrate(20)
    land()
}

def vibrate(amplitude) {
    print(amplitude)
    if (amplitude <= 0.5 and amplitude >= -0.5) {
        return
    }
    up(amplitude)
    forward(1)
    vibrate(-(amplitude / 1.1))
}
