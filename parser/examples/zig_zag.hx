def main() {
    take_off()
    up(1)
    zig_zag_to(get_gps() + (20, 20), 10)
    land()
}

def zig_zag_to(destination, steps) {
    current = get_gps()
    inc_lat = (destination.lat - current.lat) / steps
    inc_lng = (destination.lng - current.lng) / steps
    
    look_at(0)
    while (current != destination) {
        forward(inc_lat)
        right(inc_lng)
    }
}

