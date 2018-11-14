package net.marvk.lts;

import guru.nidi.graphviz.engine.Engine;
import net.marvk.lts.compiler.parser.ParseException;
import net.marvk.lts.model.LabeledTransitionSystem;
import net.marvk.lts.util.Util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Application {

    private static final String HELP_STRING = "HELP\n" +
            "\t-h\n" +
            "\tPrint this help page\n" +
            "FILES\n" +
            "\t-f file1 [file2 file3...]\n" +
            "\tSpecify input file(s), must be last argument\n" +
            "COMPOSITE\n" +
            "\t-c [name]\n" +
            "\tIf set, a composite will be created from input files\n" +
            "ALL\n" +
            "\t-a\n" +
            "\tIf set, all input files will be output, not only the composite\n" +
            "SHOW UNREACHABLE STATES\n" +
            "\t-u\n" +
            "\tIf set, renders unreachable states\n" +
            "ENGINE\n" +
            "\t-e [CIRCO|DOT|NEATO|OSAGE|TWOPI|FDP]\n" +
            "\tSet the Graphviz rendering engine\n" +
            "OUTPUT\n" +
            "\t-o file\n" +
            "\tSet the output folder\n\n" +
            "Example:\n" +
            "\tjava lts.jar -c comp -a -o results -f switch.lts lamp.lts";

    private Application() {
        throw new AssertionError("No instances of class " + Application.class);
    }

    public static void main(final String[] args) throws IOException, ParseException {
        if (args.length == 0) {
            printHelp();
            return;
        }

        Engine engine = Engine.CIRCO;
        List<LabeledTransitionSystem> lts = null;
        boolean outputAll = false;
        boolean createComposite = false;
        boolean showUnreachables = false;
        Path output = Paths.get("");
        String compositeName = null;

        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];
            switch (arg) {
                case "-h":
                    printHelp();
                    return;
                case "-f":
                    lts = getLts(args, i);
                    i = args.length;
                    break;
                case "-e":
                    i++;
                    final String arg1 = args[i];
                    engine = Engine.valueOf(arg1);
                    break;
                case "-c":
                    createComposite = true;
                    final String maybeName = args[i + 1];
                    if (!maybeName.startsWith("-")) {
                        i++;
                        compositeName = maybeName;
                    }
                    break;
                case "-u":
                    showUnreachables = true;
                    break;
                case "-a":
                    outputAll = true;
                    break;
                case "-o":
                    i++;
                    output = Paths.get(args[i]);
                    break;
                default:
                    System.err.println("Error: Unrecognized argument " + arg);
                    return;
            }
        }

        if (lts == null || lts.isEmpty()) {
            System.err.println("Error: No input specified");
            return;
        }

        if (createComposite) {
            if (lts.size() <= 1) {
                System.err.println("Warning: Only one lts specified, ignoring composite instruction");
            }

            final LabeledTransitionSystem composite = lts.get(0)
                                                         .parallelComposition(compositeName, showUnreachables, lts.subList(1, lts
                                                                 .size()));

            Util.renderLts(composite, output, engine);
        }

        if (lts.size() == 1 || outputAll) {
            for (final LabeledTransitionSystem lt : lts) {
                Util.renderLts(lt, output, engine);
            }
        }
    }

    private static List<LabeledTransitionSystem> getLts(final String[] args, final int i) throws IOException, ParseException {
        final List<LabeledTransitionSystem> result = new ArrayList<>();

        final List<Path> collect = Arrays.stream(Arrays.copyOfRange(args, i + 1, args.length))
                                         .map(s -> Paths.get(s))
                                         .collect(Collectors.toList());

        for (final Path path : collect) {

            result.add(Util.parseLts(path));
        }

        return result;
    }

    private static void printHelp() {
        System.out.println(HELP_STRING);
    }
}