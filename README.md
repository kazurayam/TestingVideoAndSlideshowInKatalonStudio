Testing WebElement in motion in Katalon Studio
=====

## What is this?

This is a [Katalon Studio](https://www.katalon.com/) project for demonstration purpose. You can clone this out to your PC and execute in Katalon Studio.

This project was developed using Katalon Studio 5.7.1.

This project was developed to propose solutions to the following 2 discussions posted in the [Katalon Forum](https://forum.katalon.com/discussions) : [Not able to verify whether a youtube video is working properly or not.](https://forum.katalon.com/discussion/9904/not-able-to-verify-whether-a-youtube-video-is-working-properly-or-not).
The original question was as follows:
>How to verify whether a youtube video is working properly or not. My scenario is to click on any video which in turn opens in a pop up. Then play the video to verify whether its playing or not and then click on pause button to verify whether it stops or not. And then close the video pop up.

## Problems to solve

You can make a web page which embeds a YouTube video. YouTube video itself is usually marked up with video element like this:
```
<video src="https://www.youtube.com/watch?v=Q80JTXYIteU">
```

And the page is expected to have a start/stop button like this:
```
<button class="ytp-play-button ytp-button" aria-label=Play>
    ...
</button>
```

When a web page with embeded video loaded, the video may autoplay on load or stay still. It depends on how the page is designed and configured. I want to verify if the video element autoplays as expected, or it stays still as designed. I want to do the verification in Katalon Studio.

## What I learned by the experiment

Verifying video is a very complicated task. My study is poor. My study has failed to conceal the complexities of video. I have given up diving into the depth anymore. 

## What happens? when you encounter a real YouTube trouble

At 10:30AM 17,Oct 2018 JST, I encountered a real YouTube trouble.
When I requested https://www.youtube.com/watch?v=Q80JTXYIteU with browser, the page was responed. But the video in the page was black and
showed a message 'sorry for inconvenience'. Wow! What a good chance to evaluate my study I got.

I ran the `Test Cases/verify-video-autoplay-example`. The test case failed AS EXPECTED.
![encounted_YouTube_trouble_1d YouTube trouble 1](docs/images/encountered_YouTube_trouble_1.png)

The test case emitted an ImageDiff as follows:
![encountered diff](docs/images/Katalon%20Studio%20-%20Quick%20start_diff%2812.09%29FAILED.png)

This incident happend to prove that my approach of *verifying YouTube video autoplay or not* is capable of detecting actual YoutTube problem.
