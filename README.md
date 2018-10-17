Testing WebElement in motion in Katalon Studio
=====

## What is this?

This is a [Katalon Studio](https://www.katalon.com/) project for demonstration purpose. You can clone this out to your PC and execute in Katalon Studio.

This project was developed using Katalon Studio 5.7.1.

This project was developed to propose solutions to the following 2 discussions posted in the [Katalon Forum](https://forum.katalon.com/discussions).

1. [Verify Image Present in slideshow](https://forum.katalon.com/discussion/9985/verify-image-present-in-slideshow-)
2. [Not able to verify whether a youtube video is working properly or not.](https://forum.katalon.com/discussion/9904/not-able-to-verify-whether-a-youtube-video-is-working-properly-or-not)

----

## Testing Slide show

### Problems to solve

Many web sites have slide show in the top page. For example, have a look at this beautiful site: [Mandelbrot Explorer](https://www.mandel.org.uk/). A slide show displays a fixed number of images switched circularly with a fixed intervals in seconds. I want to verify *if a slide show is in motion*.


### Solution

[aShot](https://github.com/yandex-qatools/ashot), WebDriver Screenshot utility, enables you to take a screenshot of a selected WebElement (e.g. `<div id="banner">`). Also aShot enables you to compare 2 images of the WebElement to figure out how much different they are.

Let me suppose a slide show has 4 images displayed circularly with 6 seconds interval. I will take screenshot of the `<div id="banner">` element using aShot with 6 seconds delay. Then I will compare 4 pairs of images: img0-img1, img1-img2, img2-img3, img3-img0. All of the pairs are expected to be *different enough* when the slide show is in motion. If one or more pairs are found *not different enough*, then the test should fail and notify me of the failure.

### Demonstration, how to run it

This Katalon Studio project provides a set of Custom Keywords in Katalon Studio, which wrap the [aShot](https://github.com/yandex-qatools/ashot) API. Also the project provides an example Test Case which shows how to make use of the keywords.


### Code description

### Thanks

Special thanks to [Mandelbrot Explorer](https://www.mandel.org.uk/). I picked up this as an example of web site with slide show. I found it in the page of Drupal Module [Views Slideshow](https://www.drupal.org/project/views_slideshow).

----
## Testing YouTube Video

### Problems to solve

### Solution

### Demonstration, how to run it

### Code description

1. Want to verify a YouTube video is actually autoplaying if the target web page is designed to autoplay the video. Also want to verify a YouTube video is staying still if the target web page is desinged NOT to autoplay it.
2. Want to verify a
