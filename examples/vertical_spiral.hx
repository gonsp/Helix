def vertical_spiral(max_height) {
	[_, _, height] = getPos()
	print(height)
	if height >= max_height {
	    move([0, 0, height])
    } else {
		forward(10)
		rotate(-90)
		up(1)
		vertical_spiral(max_height)
	}
}

def main() {
    takeOff(5)
    right(5)
    forward(5)
    rotate(-90)
    vertical_spiral(15)
    land()
}
