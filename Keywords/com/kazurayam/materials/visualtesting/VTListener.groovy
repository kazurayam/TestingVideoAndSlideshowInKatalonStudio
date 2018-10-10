package com.kazurayam.materials.visualtesting


interface VTListener {

	void info(String message)

	void failed(String message)

	void fatal(String message)
}

