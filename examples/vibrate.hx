def main() {
    take_off()
    up(1)
    
    vibrate(20)

    land()
}

def vibrate(amplitude) {
    if (amplitude <= 0.5) {
        return
    }

    up(amplitude)
    //vibrate(-(amplitude / 1.5))
}

