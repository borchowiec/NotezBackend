package com.borchowiec.notez.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SongAspect {
    Logger logger = Logger.getLogger(this.getClass().getName());

    @Before("execution(* com.borchowiec.notez.controller.SongController.addSong(..))")
    public void logAboutTryingToAddSong() {
        logger.info("Attempting to add song.");
    }

    @AfterThrowing(value = "execution(* com.borchowiec.notez.controller.SongController.addSong(..))", throwing = "e")
    public void logErrorOfAddingNewSong(Exception e) {
        logger.error("Could not add song. " + e.getMessage());
    }

    @AfterReturning("execution(* com.borchowiec.notez.controller.SongController.addSong(..))")
    public void logNewSongAdded() {
        logger.info("Song has been added.");
    }


    @Before("execution(* com.borchowiec.notez.controller.SongController.get*(..))")
    public void logAboutTryingToGetSongs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Trying to: ").append(joinPoint.getSignature().getName()).append(" ");
        if (args.length != 0) {
            stringBuilder
                    .append(((CodeSignature) joinPoint.getSignature()).getParameterNames()[0])
                    .append("=")
                    .append(args[0].toString());
        }

        logger.info(stringBuilder.toString());
    }

    @AfterThrowing(value = "execution(* com.borchowiec.notez.controller.SongController.get*(..))", throwing = "e")
    public void logErrorOfGettingSongs(JoinPoint joinPoint, Exception e) {
        Object[] args = joinPoint.getArgs();
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Error: ").append(joinPoint.getSignature().getName()).append(" ");
        if (args.length != 0) {
            stringBuilder
                    .append(((CodeSignature) joinPoint.getSignature()).getParameterNames()[0])
                    .append("=")
                    .append(args[0].toString())
                    .append(". ");
        }
        stringBuilder.append(e.getMessage());

        logger.info(stringBuilder.toString());
    }

    @AfterReturning(value = "execution(* com.borchowiec.notez.controller.SongController.get*(..))")
    public void logErrorOfGettingSongs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Done: ").append(joinPoint.getSignature().getName()).append(" ");
        if (args.length != 0) {
            stringBuilder
                    .append(((CodeSignature) joinPoint.getSignature()).getParameterNames()[0])
                    .append("=")
                    .append(args[0].toString());
        }

        logger.info(stringBuilder.toString());
    }
}
