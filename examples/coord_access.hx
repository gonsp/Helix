def main() {
    take_off(10)
    [ini_lat, ini_lng, ini_alt] = get_gps()
    move([ini_lat, ini_lng + 10, 20])
    [sec_lat, sec_lng, sec_alt] = get_gps()
    next = [sec_lat, sec_lng, 40]
    move([ini_lat, ini_lng, 30])
    move(next)
    land()
}

