package util.runtime.jvm;

import java.nio.file.Path;

import util.runtime.BaseProgramArgument;
import util.runtime.ProgramArgument;


/**
 * Factory methods to build a command to launch a JVM.
 */
public class JvmCmdFactory {

    /**
     * Builds a command to launch a JVM with a main class.
     * @param jvmPath the path to the JVM executable.
     * @param classPath the class path to pass to the JVM.
     * @param mainClass the main class to run.
     * @return a command builder that can be further customized (e.g. setting
     * system properties or specifying main method arguments) before getting
     * the {@link JvmCmdBuilder#tokens() tokens} that make up the command line.
     * @throws NullPointerException if any of the arguments is {@code null}.
     */
    public static JvmCmdBuilder java(ProgramArgument<Path> jvmPath,
                                     ClassPathJvmArg classPath, 
                                     ProgramArgument<String> mainClass) {
        return new MainClassCmdBuilder(jvmPath, classPath, mainClass);
    }

    /**
     * Builds a command to launch a JVM with a main class.
     * The JVM in question will be either another instance of the this running
     * JVM if its absolute path can be determined, or the JVM in the system 
     * path, if any.
     * @param classPath the class path to pass to the JVM.
     * @param mainClass the main class to run.
     * @return a command builder that can be further customized (e.g. setting
     * system properties or specifying main method arguments) before getting
     * the {@link JvmCmdBuilder#tokens() tokens} that make up the command line.
     * @throws NullPointerException if any of the arguments is {@code null}.
     */
    public static JvmCmdBuilder java(ClassPathJvmArg classPath,
                                     ProgramArgument<String> mainClass) {
        return java(jvm(), classPath, mainClass);
    }

    /**
     * Builds a command to launch a JVM with a jar file.
     * @param jvmPath the path to the JVM executable.
     * @param appToRun the jar file containing the application to run.
     * @return a command builder that can be further customized (e.g. setting
     * system properties or specifying main method arguments) before getting
     * the {@link JvmCmdBuilder#tokens() tokens} that make up the command line.
     * @throws NullPointerException if any of the arguments is {@code null}.
     */
    public static JvmCmdBuilder java(ProgramArgument<Path> jvmPath,
                                     JarJvmArg appToRun) {
        return new JarCmdBuilder(jvmPath, appToRun);
    }

    /**
     * Builds a command to launch a JVM with a jar file.
     * The JVM in question will be either another instance of the this running
     * JVM if its absolute path can be determined, or the JVM in the system 
     * path, if any.
     * @param appToRun the jar file containing the application to run.
     * @return a command builder that can be further customized (e.g. setting
     * system properties or specifying main method arguments) before getting
     * the {@link JvmCmdBuilder#tokens() tokens} that make up the command line.
     * @throws NullPointerException if any of the arguments is {@code null}.
     */
    public static JvmCmdBuilder java(JarJvmArg appToRun) {
        return java(jvm(), appToRun);
    }

    /**
     * Builds a an argument with the absolute path of executable of the 
     * currently running JVM if that could be determined; otherwise falls
     * back to the JVM in the system path.
     * @return the JVM path.
     */
    public static ProgramArgument<Path> jvm() {
        Path thisJvmOrOneInSysPath = JvmLocator.findCurrentJvmExecutable()
                                               .orElse(JvmName.find().toPath());
        return new BaseProgramArgument<>(thisJvmOrOneInSysPath);
    }
    
    /**
     * Attempts to build a an argument with the absolute path of executable of
     * the currently running JVM.
     * @return this JVM's path.
     * @throws java.util.NoSuchElementException if the path could not be
     * determined.
     */
    public static ProgramArgument<Path> thisJvm() {
        return JvmLocator.findCurrentJvmExecutable()
                         .map(BaseProgramArgument<Path>::new)
                         .get();
    }
    
}
