package com.eg.testffmpegcliwrapper;

import com.alibaba.fastjson.JSON;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TestFfmpeg {
    public static void main(String[] args) throws IOException {
        FFmpeg ffmpeg = new FFmpeg("C:\\mysofts\\ffmpeg.exe");
        FFprobe ffprobe = new FFprobe("C:\\mysofts\\ffprobe.exe");
        FFmpegProbeResult probeResult = ffprobe.probe(
                "D:\\BaiduNetdiskDownload\\2021.09.03 新浪微博实习.mp4");

        FFmpegFormat format = probeResult.getFormat();
        System.out.println(JSON.toJSONString(probeResult));


        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        FFmpegProbeResult in = ffprobe.probe("D:\\BaiduNetdiskDownload\\2021.09.03 新浪微博实习.mp4");

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(in) // Or filename
                .addOutput("D:\\BaiduNetdiskDownload\\2021.09.03 新浪微博实习.flv")
                .done();

        FFmpegJob job = executor.createJob(builder, new ProgressListener() {
            // Using the FFmpegProbeResult determine the duration of the input
            final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

            @Override
            public void progress(Progress progress) {
                double percentage = progress.out_time_ns / duration_ns;

                // Print out interesting information about the progress
                System.out.printf(
                        "[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx%n",
                        percentage * 100,
                        progress.status,
                        progress.frame,
                        FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
                        progress.fps.doubleValue(),
                        progress.speed
                );
            }
        });

        job.run();

    }
}
