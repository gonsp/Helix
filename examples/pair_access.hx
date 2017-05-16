def main() {
    take_off()
    up(1)
    
    [ini_lat, ini_lng] = get_gps()
    move([ini_lat, ini_lng + 10])

    land()
}

