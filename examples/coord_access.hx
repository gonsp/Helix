def main() {
    takeOff(10)
    [ini_lat, ini_lng, ini_alt] = getPos()
    move([ini_lat, ini_lng + 10, 20])
    [sec_lat, sec_lng, sec_alt] = getPos()
    next = [sec_lat, sec_lng, 40]
    move([ini_lat, ini_lng, 30] + [0, 0, next.alt])
    move(next)
    print(next.lat)
    print(next.lng)
    [_, _, height] = next
    print(height)
    land()
}

