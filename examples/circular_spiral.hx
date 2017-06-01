def circular_spiral(circles, is_up) {
    radius = 5
	while(circles > 0) {
	    if(getDir() < radius) {
	    	circles = circles-1
	    }
	    rotate(-radius)
        forward(1)
        if(is_up) {
            up(0.5)
        } else {
            down(0.5)
            pos = getPos()
            if(pos.alt < 5) {
                return
            }
        }
	}
}

def main() {
    takeOff(100)
    circular_spiral(5, true)
    forward(100)
    circular_spiral(5, false)
    land()
}
