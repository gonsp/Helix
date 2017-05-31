def main() {
    takeOff()
    up(1)
    zig_zag_to(getPos() + [20, 20, 5], 10)
    land()
}

def zig_zag_to(destination, steps) {
    current = getPos()
    inc_lat = (destination.lat - current.lat) / steps
    inc_lng = (destination.lng - current.lng) / steps
    
    lookAt(0)
    while (current != destination) {
        forward(inc_lat)
        right(inc_lng)
    }
}

