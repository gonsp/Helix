def spiral(level) {
	if level <= 0 {
		down(5)
		forward(5)
    }
    else {
		forward(10)
		rotate(-90)
		up(1)
		spiral(level-1)
	}
}

def main() {
    take_off(5)
    spiral(5)
    land()
}
