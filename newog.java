import java.io.IOException;
import java.io.PrintStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class newog {

    private static final String[] ALLTEAMS = { "DAL", "MEM", "ORL", "MIA", "MIN", "TOR", "CHA", "NOH", "GSW", "SAS",
            "HOU", "ATL", "WAS", "PHO", "SAC", "LAL", "LAC", "CLE", "CHI", "DET", "IND", "UTA", "POR", "DEN", "NYK",
            "NJN", "BOS", "PHI", "OKC", "MIL" };

    public static void main(String[] args) throws IOException {

        // Year that ABA collapsed and left only NBA
        int year = 1976;

        for (int i = 0; i < ALLTEAMS.length; i++) {
            // Adjust for teams that were added after 1976
            if (ALLTEAMS[i].equals("DAL")) {
                year = 1980;
            } else if (ALLTEAMS[i].equals("MEM")) {
                year = 1995;
            } else if (ALLTEAMS[i].equals("ORL")) {
                year = 1989;
            } else if (ALLTEAMS[i].equals("MIA")) {
                year = 1988;
            } else if (ALLTEAMS[i].equals("MIN")) {
                year = 1989;
            } else if (ALLTEAMS[i].equals("TOR")) {
                year = 1995;
            } else if (ALLTEAMS[i].equals("CHA")) {
                year = 1988;
            } else if (ALLTEAMS[i].equals("NOH")) {
                year = 2002;
            } else {
                year = 1976;
            }

            while (year <= 2018) {
                String team = ALLTEAMS[i];

                // Adjust for team name changes
                if (team.equals("MEM")) {
                    if (year <= 2000) {
                        team = "VAN";
                    } else {
                        team = "MEM";
                    }
                } else if (team.equals("CHA")) {
                    if (year <= 2001) {
                        team = "CHH";
                    } else if (year == 2002) {
                        year = 2004;
                        team = "CHA";
                    } else if (year >= 2014) {
                        team = "CHO";
                    }
                } else if (team.equals("NOH")) {
                    if (year == 2005 || year == 2006) {
                        team = "NOK";
                    } else if (year >= 2013) {
                        team = "NOP";
                    }
                } else if (team.equals("WAS")) {
                    if (year <= 1996) {
                        team = "WSB";
                    } else {
                        team = "WAS";
                    }
                } else if (team.equals("SAC")) {
                    if (year <= 1984) {
                        team = "KCK";
                    } else {
                        team = "SAC";
                    }
                } else if (team.equals("LAC")) {
                    if (year <= 1977) {
                        team = "BUF";
                    } else if (year <= 1983) {
                        team = "SDC";
                    } else {
                        team = "LAC";
                    }
                } else if (team.equals("UTA")) {
                    if (year <= 1978) {
                        team = "NOJ";
                    } else {
                        team = "UTA";
                    }
                } else if (team.equals("NJN")) {
                    if (year == 1976) {
                        team = "NYN";
                    } else if (year >= 2012) {
                        team = "BRK";
                    } else {
                        team = "NJN";
                    }
                } else if (team.equals("OKC")) {
                    if (year <= 2007) {
                        team = "SEA";
                    } else {
                        team = "OKC";
                    }
                }

                String nextYear = Integer.toString(year + 1).substring(2);

                Path path = Paths.get(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear);
                // Checks if directories exist
                if (!Files.exists(path)) {
                    try {
                        Files.createDirectories(path);
                    } catch (IOException e) {
                        // Fail to create directories
                        e.printStackTrace();
                    }
                }

                File f = new File(
                        ".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\" + "season_summary.tsv");

                // File f = new File("C:\\Users\\nicol\\OneDrive\\Documents\\NBA\\Every NBA Team
                // Stat\\" + "NYK" + "\\"
                // + "2018" + "-" + "2019" + ".txt");

                PrintStream output = new PrintStream(f);

                // URL u = new URL("https://www.basketball-reference.com/teams/" + team + "/" +
                // (year + 1) + ".html");

                URL u = new URL("https://www.basketball-reference.com/teams/" + "LAC" + "/" + "2010" + ".html");

                Scanner scan = new Scanner(u.openStream());

                // Make TSV of Season Summary
                printTeamSummaryTSV(scan, output);

                // Make New TSV File in same year for the Roster
                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\" + "roster.tsv");
                output = new PrintStream(f);

                // Make Roster TSV File
                printRosterTSV(scan, output);

                // Loop until finds section where Injury Report Would be
                // eg: ...id="assistant_coaches_link"...<h2>Assistant Coaches and Staff </h2>...
                // OR ...id = "injury_link"...</span><h2>Injury Report</h2>...
                String line = scan.nextLine();
                while (!line.contains("assistant_coaches_link") && !line.contains("injury_link")) {
                    line = scan.nextLine();
                }
                // Make Injury Report TSV if there is an Injury
                if (line.contains("injury_link")) {
                    f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\"
                            + "injury_report.tsv");
                    output = new PrintStream(f);
                    printInjuryReportTSV(scan, output);
                }

                // Make New TSV File in same year for the Roster
                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\"
                        + "assistant_coaches.tsv");
                output = new PrintStream(f);

                // Make TSV of Asisstant Coaches and Staff
                printAssistantCoachesAndStaffTSV(scan, output);

                // New TSV File for Team Stats
                f = new File(
                        ".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\" + "team_stats.tsv");
                output = new PrintStream(f);
                // New TSV file for Opponent Stats
                f = new File(
                        ".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\" + "opponent_stats.tsv");

                // Make TSV of The Team and Opponent Stats Table
                // Team and Opponent Stats stores in seperate TSV files
                printTeamAndOpponentStatsTSV(scan, output, new PrintStream(f));

                // New TSV File for Team Misc Stats
                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\" + "team_misc.tsv");
                output = new PrintStream(f);

                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\"
                        + "advanced_team_misc.tsv");
                PrintStream output2 = new PrintStream(f);

                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\"
                        + "offensive_four_factors_team_misc.tsv");
                PrintStream output3 = new PrintStream(f);

                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\"
                        + "defensive_four_factors_team_misc.tsv");

                // Make TSV of Team Misc Stats
                printTeamMiscTSV(scan, output, output2, output3, new PrintStream(f), year);
                output2.close(); // Close PrintStream
                output3.close(); // Close PrintStream

                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\" + "per_game.tsv");
                output = new PrintStream(f);

                // Make TSV of Per Game Stats
                printPerGameTSV(scan, output, year);

                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\" + "totals.tsv");
                output = new PrintStream(f);

                // Make TSV of Team Totals
                printTotalsTSV(scan, output, year);

                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\" + "per_36.tsv");
                output = new PrintStream(f);

                // Make TSV of Per 36
                printPer36TSV(scan, output, year);

                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\" + "per_100.tsv");
                output = new PrintStream(f);

                // Make TSV of Per 100 Poss
                printPer100TSV(scan, output, year);

                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\" + "test.txt");
                output = new PrintStream(f);

                output.println();

                printPerGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay("ADVANCED", scan, output,
                        year);

                output.println();

                line = scan.nextLine();

                while (!line.contains("<tr>") && !line.contains("<!-- global.nonempty_tables_num:")
                        && !line.contains("\"all_salaries2\"")) {
                    line = scan.nextLine();
                }

                if (year >= 2000) { // Shooting and Play by Play available as of 2000-2001 season

                    printShootingHeader(output);

                    printPerGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay("SHOOTING", scan,
                            output, year);

                    output.println();

                    while (!line.contains("<tr>")) {
                        line = scan.nextLine();
                    }

                    printPlayByPlayHeader(output);

                    printPerGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay("PLAY BY PLAY", scan,
                            output, year);

                    line = scan.nextLine();

                    output.println();

                }

                while (!line.contains("<tr>") && !line.contains("\"all_salaries2\"")
                        && !line.contains("<!-- global.nonempty_tables_num:")) {
                    line = scan.nextLine();
                }

                if (line.contains("<tr>")) { // Get Playoff Stats
                    printPerGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay("PLAYOFFS TOTALS",
                            scan, output, year);

                    output.println();

                    printPerGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay("PLAYOFFS PER GAME",
                            scan, output, year);

                    output.println();

                    printPerGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay(
                            "PLAYOFFS PER 36 MINUTES", scan, output, year);

                    output.println();

                    printPerGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay(
                            "PLAYOFFS PER 100 POSSESSIONS", scan, output, year);

                    output.println();

                    printPerGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay("PLAYOFFS ADVANCED",
                            scan, output, year);

                    output.println();

                    if (year >= 2000) { // Shooting and Play by Play available as of 2000-2001 season

                        output.print("PLAYOFFS ");

                        printShootingHeader(output);

                        printPerGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay("SHOOTING", scan,
                                output, year);

                        output.println();

                        while (!line.contains("<tr>")) {
                            line = scan.nextLine();
                        }

                        output.print("PLAYOFFS ");

                        printPlayByPlayHeader(output);

                        printPerGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay("PLAY BY PLAY",
                                scan, output, year);

                        output.println();

                    }
                }

                while (!line.contains("\"all_salaries2\"") && !line.contains("<!-- global.nonempty_tables_num:")) {
                    line = scan.nextLine();
                }

                if (line.contains("\"all_salaries2\"")) {
                    while (!line
                            .contains("<div id=\"all_salaries2\" class=\"table_wrapper setup_commented commented\">")) {
                        line = scan.nextLine();
                    }

                    output.println("SALARY");

                    output.println();

                    printSalary(output, scan);
                }
                year++;
            }
        }
    }

    public static void printSalary(PrintStream output, Scanner scan) {
        output.print("|");
        output.print("Rk "); // Print out "Rk" Header
        output.print("|");

        output.print("Name                    "); // Print out "Name" Header
        output.print("|");

        output.print("      Salary"); // Print out "Name" Header
        output.println("|");

        String line = scan.nextLine();
        while (!line.contains("\"center \" data-stat=\"ranker\"")) {
            line = scan.nextLine();
        }
        while (line.contains("\"center \" data-stat=\"ranker\"")) {
            output.print("|");
            line = customBracketBreaker(line, 2);
            int bracket1 = line.indexOf("<");
            output.print(line.substring(0, bracket1)); // Print Rank
            for (int i = 0; i < 3 - line.substring(0, bracket1).length(); i++) {
                output.print(" ");
            }
            output.print("|");

            line = customBracketBreaker(line, 2); // Some Players do not have Basketball Reference Link
            if (line.startsWith("<")) {
                line = customBracketBreaker(line, 1);
            }
            bracket1 = line.indexOf("<");
            output.print(line.substring(0, bracket1)); // Print Name
            for (int i = 0; i < 24 - line.substring(0, bracket1).length(); i++) {
                output.print(" ");
            }
            output.print("|");

            line = customBracketBreaker(line, 2); // Some Players do not have Basketball Reference Link
            if (!line.startsWith("$")) {
                line = customBracketBreaker(line, 1);
            }
            bracket1 = line.indexOf("<");
            output.print(line.substring(0, bracket1)); // Print Salary
            for (int i = 0; i < 12 - line.substring(0, bracket1).length(); i++) {
                output.print(" ");
            }
            output.println("|");
            line = scan.nextLine();
        }
    }

    public static void printPlayByPlayHeader(PrintStream output) {
        output.println("PLAY-BY-PLAY");
        output.println();
        output.print("|");
        for (int j = 0; j < 32; j++) { // Empty Space before Totals
            output.print(" ");
        }
        output.print("|");
        output.print("Totals   ");
        output.print("|");
        output.print("      Position Estimate      ");
        output.print("|");
        output.print("+/- Per 100 Poss.");
        output.print("|");
        output.print("    Turnovers    ");
        output.print("|");
        output.print("Fouls Committed");
        output.print("|");
        output.print("  Fouls Drawn  ");
        output.print("|");
        output.print("      Misc.      ");
        output.print("|");
    }

    public static void printShootingHeader(PrintStream output) {
        output.println("SHOOTING");
        output.println();
        output.print("|");
        for (int j = 0; j < 126; j++) { // Empty Space before 2-Pt Field Goals
            output.print(" ");
        }
        output.print("|");
        output.print(" 2-Pt Field Goals ");
        output.print("|");
        output.print("       3-Pt Field Goals      ");
        output.println("|");

        output.print("|");
        for (int j = 0; j < 32; j++) { // Empty Space before Totals
            output.print(" ");
        }
        output.print("|");
        output.print("Totals   ");
        output.print("|");
        for (int j = 0; j < 5; j++) { // Empty Space
            output.print(" ");
        }
        output.print("|");
        for (int j = 0; j < 5; j++) { // Empty Space
            output.print(" ");
        }
        output.print("|");
        output.print("        % of FGA by Distance       ");
        output.print("|");
        for (int j = 0; j < 5; j++) { // Empty Space
            output.print(" ");
        }
        output.print("|");
        output.print("    FG% by Distance    ");
        output.print("|");
        for (int j = 0; j < 5; j++) { // Empty Space
            output.print(" ");
        }
        output.print("|");
        for (int j = 0; j < 6; j++) { // Empty Space
            output.print(" ");
        }
        output.print("|");
        output.print("   Dunks   ");
        output.print("|");
        for (int j = 0; j < 6; j++) { // Empty Space
            output.print(" ");
        }
        output.print("|");
        output.print("   Corner    ");
        output.print("|");
        output.print(" Heaves ");
        output.print("|");
    }

    public static String statMaker(String line, int length, int bracketNumber, PrintStream output) {
        line = customBracketBreaker(line, bracketNumber);
        int bracket1 = line.indexOf("<");
        output.print(line.substring(0, bracket1));
        for (int i = 0; i < length - line.substring(0, bracket1).length(); i++) {
            output.print(" ");
        }

        output.print("|");
        return line;
    }

    public static String headerMaker(int length, String line, int bracketNumber, PrintStream output) {
        if (bracketNumber == 1) {
            line = bracketBreaker(line);
            output.print(line);
            for (int i = 0; i < length - line.length(); i++) {
                output.print(" ");
            }
        } else {
            line = customBracketBreaker(line, bracketNumber);
            int bracket1 = line.indexOf("<");
            output.print(line.substring(0, bracket1));
            for (int i = 0; i < length - line.substring(0, bracket1).length(); i++) {
                output.print(" ");
            }
        }
        output.print("|");
        return line;
    }

    public static void printTeamSummaryTSV(Scanner scan, PrintStream output) {
        List<String> headers = new ArrayList<String>(); // Holds all headers for TSV
        List<String> values = new ArrayList<String>(); // Holds all values for the headers for TSV
        String temp; // Concatinate values to be places in values
        // Variables for Regex
        Pattern pattern;
        Matcher matcher;

        String line = scan.nextLine();
        // Loop until Finds Record Section
        // eg: <strong>Record:</strong>
        while (!line.contains("Record:")) {
            line = scan.nextLine();
        }
        headers.add("Record"); // Get Record Header
        // Loop until Finds Actual Record Using Regex
        // eg: 17-65, 15th in <a href...
        // ....Eastern Conference
        pattern = Pattern.compile("[0-9]{1,2}-[0-9]{1,2}?"); // Regex stuff
        matcher = pattern.matcher(line);
        while (!matcher.find()) {
            line = scan.nextLine();
            matcher = pattern.matcher(line);
        }
        // Get rid of tags to get record and text info on Record
        temp = line.substring(0, line.indexOf("<"));
        temp += getElementWithinTag(line);
        pattern = Pattern.compile("Division|Conference");
        matcher = pattern.matcher(line);
        while (!matcher.find()) {
            matcher = pattern.matcher(line);
            line = scan.nextLine();
        }
        temp += " " + line;
        values.add(temp.trim()); // Put Record and text info into array

        // Loop to next value
        // eg: <strong>Last Game:</strong>
        // OR: <strong>Coach:</strong>
        while (!line.contains("<strong>")) {
            line = scan.nextLine();
        }

        if (line.contains("Last Game:")) {
            headers.add("Last Game"); // Puts in Last Game Title
            // Loop until getting to actual Score
            // eg: L 89-115
            pattern = Pattern.compile("[0-9]{1,3}-[0-9]{1,3}");
            matcher = pattern.matcher(line);
            while (!matcher.find()) {
                line = scan.nextLine();
                matcher = pattern.matcher(line);
            }
            temp = line.trim();
            temp += " " + scan.nextLine().trim();
            temp += " " + scan.nextLine().trim();
            values.add(temp.trim()); // Add Last Game Info to Array

            // Loop to next value
            // eg: <strong>Coach:</strong> <a href...
            while (!line.contains("<strong>")) {
                line = scan.nextLine();
            }
        }

        // Puts in Coaches Title
        headers.add("Coach");
        // Next Part gets all coaches
        pattern = Pattern.compile(">(\\p{L}+ \\p{L}+)<.{1,8}\\(([0-9]{1,2}-[0-9]{1,2})\\)");
        matcher = pattern.matcher(line);
        // Get first coach and Record
        if (matcher.find()) {
            temp = matcher.group(1);
            temp += " " + matcher.group(2);
        }
        // Get other coaches if applicable, space seperated
        while (matcher.find()) {
            temp += " " + matcher.group(1);
            temp += " " + matcher.group(2);
        }
        values.add(temp.trim()); // Put coaches info into array

        line = scan.nextLine();
        // Loop to next value
        // eg: <strong>Executive:</strong> <a href...
        while (!line.contains("<strong>")) {
            line = scan.nextLine();
        }
        headers.add("Executive");// Add in Executive to headers
        pattern = Pattern.compile(">(\\p{L}+ \\p{L}+)<"); // Regex name
        matcher = pattern.matcher(line);
        // Get first Executive
        if (matcher.find()) {
            temp = matcher.group(1);
        }
        // Get other Executives if applicable, space seperated
        while (matcher.find()) {
            temp += " " + matcher.group(1);
        }
        values.add(temp.trim());

        line = scan.nextLine();
        // Loop to next value
        // eg: <strong>PTS/G:</strong>
        while (!line.contains("<strong>")) {
            line = scan.nextLine();
        }
        headers.add("PTS/G"); // Add PTS/G To headers

        // Add Actual PTS/G Value
        values.add(scan.nextLine().trim());

        line = scan.nextLine();
        // Loop to next value
        // eg: <strong>Opp PTS/G:</strong>
        while (!line.contains("<strong>")) {
            line = scan.nextLine();
        }
        headers.add("Opp PTS/G");

        // Add actual Opponent PTS/G
        values.add(scan.nextLine().trim());

        line = scan.nextLine();
        // Loop to next value
        // eg: <strong><a href = ...
        while (!line.contains("<strong>")) {
            line = scan.nextLine();
        }
        headers.add("SRS"); // Add SRS to Array

        pattern = Pattern.compile(".* (.+\\(.*\\))");
        matcher = pattern.matcher(line);
        matcher.find();
        // Add actual SRS Value
        values.add(matcher.group(1).trim());

        line = scan.nextLine();
        // Loop to next value
        // eg: <strong><a href = ...
        while (!line.contains("<strong>")) {
            line = scan.nextLine();
        }
        headers.add("Pace"); // Add Pace to Array

        pattern = Pattern.compile(".* (.+\\(.*\\))");
        matcher = pattern.matcher(line);
        matcher.find();
        // Add actual Pace Value
        values.add(matcher.group(1).trim());

        line = scan.nextLine();

        // Loop to next value
        // eg: <strong><a href = ...
        while (!line.contains("<strong>")) {
            line = scan.nextLine();
        }
        headers.add("Off Rtg"); // Add Off Rtg to Array

        pattern = Pattern.compile(".* (.+\\(.*\\))");
        matcher = pattern.matcher(line);
        matcher.find();
        // Add Actual Off Rtg
        values.add(matcher.group(1).trim());

        line = scan.nextLine();

        // Loop to next value
        // eg: <strong><a href = ...
        while (!line.contains("<strong>")) {
            line = scan.nextLine();
        }
        headers.add("Def Rtg"); // Add Def Rtg to Array

        pattern = Pattern.compile(".* (.+\\(.*\\))");
        matcher = pattern.matcher(line);
        matcher.find();
        // Add actual Def Rtg
        values.add(matcher.group(1).trim());

        line = scan.nextLine();

        // Loop to next value
        // eg: <strong><a href = ...
        while (!line.contains("<strong>")) {
            line = scan.nextLine();
        }
        headers.add("Expected W-L"); // Add Expected W-L to Array

        pattern = Pattern.compile(".* (.+\\(.*\\))");
        matcher = pattern.matcher(line);
        matcher.find();
        // Add actual Expected W-L To Array
        values.add(matcher.group(1).trim());

        line = scan.nextLine();

        // Loop to next value
        // eg: <strong>Preseason Odds:</strong>
        // OR: <strong>Arena:</strong>
        while (!line.contains("<strong>")) {
            line = scan.nextLine();
        }

        if (line.contains("Preseason Odds:")) {
            headers.add("Preseason Odds"); // Add Preseason Odds to headers

            pattern = Pattern.compile(">(.* [\\+-]?[0-9]{1,})<");
            matcher = pattern.matcher(line);
            // Find Actual Championship Value
            // eg: <a href =...
            // ....28.5
            while (!matcher.find()) {
                line = scan.nextLine();
                matcher = pattern.matcher(line);
            }
            temp = matcher.group(1);
            temp += ", Over-Under " + scan.nextLine().trim();
            // Add Championship odd and Over Under Values to Array
            values.add(temp.trim());

            // Loop to next value
            // eg: <strong>Arena:</strong>
            while (!line.contains("<strong>")) {
                line = scan.nextLine();
            }
        }

        headers.add("Arena"); // Add Arena to headers
        // Add actual Arena To Array
        values.add(scan.nextLine().trim());

        line = scan.nextLine();

        // Loop to Attendance or until end of values if no attendance
        // eg: <strong>Attendance</strong>
        // OR </p>
        while (!line.contains("<strong>") && !line.contains("</p>")) {
            line = scan.nextLine();
        }
        // Attendance is available
        if (line.contains("<strong>")) {
            headers.add("Attendance");
            // Add actual Attendance To Array
            values.add(scan.nextLine().replace(",", "").trim());
        }
        // Put all Values into TSV File
        for (int i = 0; i < headers.size() - 1; i++) {
            output.print(headers.get(i) + "\t");
        }
        output.println(headers.get(headers.size() - 1));

        for (int i = 0; i < values.size() - 1; i++) {
            output.print(values.get(i) + "\t");
        }
        output.println(values.get(values.size() - 1));
        output.close();
    }

    public static void printRosterTSV(Scanner scan, PrintStream output) {
        // Lists to store vales for TSV. Each list for values is a player.
        // Temp for concatinating strings to put into values.
        // Pattern and matcher are for regex.
        List<String> headers = new ArrayList<String>();
        List<List<String>> values = new ArrayList<List<String>>();
        String temp;
        Pattern pattern;
        Matcher matcher;
        String line = scan.nextLine();
        // Loop Until finding Roster Table
        // eg: <th aria-label="Uniform "...
        // ....<th aria-label="Player"...
        while (!line.contains("<th aria")) {
            line = scan.nextLine();
        }
        // Loop through All Headers and add them to Header Array
        while (!line.contains("</tr>")) {
            // Replace Blank Header with 'Birth Country'
            if (line.contains("&nbsp;")) {
                headers.add("Birth Country");
            } else {
                headers.add(getElementWithinTag(line).trim());
            }
            line = scan.nextLine();
        }

        // Loop until finding First Player on Roster
        // eg: <tr ><th scope="row" class="center"...
        while (!line.contains("<th scope=\"row\"")) {
            line = scan.nextLine();
        }
        // Loops through all players and inputs the values
        // Loops until no more rows/players
        while (line.contains("<th scope=\"row\"")) {
            // Regex for getting things in between tags
            pattern = Pattern.compile(">([[0-9]-,\\.'\\p{L}& ]+)<");
            matcher = pattern.matcher(line);
            // Loops through string for one player to find all values
            // Gets rid of opening and closing tag
            values.add(new ArrayList<String>()); // new player
            // List holding last added player's values
            List<String> currentPlayer = values.get(values.size() - 1);
            while (matcher.find()) {
                currentPlayer.add(matcher.group(1).trim());
            }
            // If Player went to multiple colleges, throw them all into one observation
            // eg: CollegeOne CollegeTwo
            // not: CollegeOne, CollegeTwo
            if (currentPlayer.size() > headers.size()) {
                temp = currentPlayer.get(headers.size() - 1); // First College
                while (currentPlayer.size() != headers.size()) { // Loop Through any additional colleges
                    if (!currentPlayer.get(headers.size()).contains(",")) { // Remove blank Spaces between colleges
                        temp += ", " + currentPlayer.get(headers.size());
                    }
                    currentPlayer.remove(headers.size());
                }
                currentPlayer.set(headers.size() - 1, temp); // Add String with all colleges
            }
            // If Player did not play in US College then it will be missing the value
            // This will add a blank value if it is missing
            if (currentPlayer.size() < headers.size()) {
                currentPlayer.add("");
            }
            // Capitalize Country of Birth
            int birthCountryIndex = headers.indexOf("Birth Country");
            currentPlayer.set(birthCountryIndex, currentPlayer.get(birthCountryIndex).toUpperCase());
            // Change Rookie "R" to 0 so that column is all ints in TSV
            int rookieIndex = currentPlayer.size() - 2;
            if (currentPlayer.get(rookieIndex).equals("R")) {
                currentPlayer.set(rookieIndex, "0");
            }
            line = scan.nextLine();
        }

        // Print Out Headers and values to TSV
        for (int i = 0; i < headers.size() - 1; i++) {
            output.print(headers.get(i) + "\t");
        }
        output.println(headers.get(headers.size() - 1));

        for (int i = 0; i < values.size(); i++) {
            List<String> currentPlayer = values.get(i);
            for (int j = 0; j < currentPlayer.size() - 1; j++) {
                output.print(currentPlayer.get(j) + "\t");
            }
            output.println(currentPlayer.get(currentPlayer.size() - 1));
        }
        output.close();
    }

    public static void printInjuryReportTSV(Scanner scan, PrintStream output) {
        // Lists to store vales for TSV. Each list for values is a player.
        // Pattern and matcher are for regex.
        List<String> headers = new ArrayList<String>();
        List<List<String>> values = new ArrayList<List<String>>();
        Pattern pattern;
        Matcher matcher;
        String line = scan.nextLine();
        // Loop Until finding Injury Table
        // eg: <th aria-label="Player "...
        // ....<th aria-label="Team"...
        while (!line.contains("<th aria")) {
            line = scan.nextLine();
        }
        // Loop through All Headers and add them to Header Array
        while (!line.contains("</tr>")) {
            // Replace Blank Header with 'Birth Country'
            headers.add(getElementWithinTag(line).trim());
            line = scan.nextLine();
        }

        // Loop until finding First Player on Roster
        // eg: <tr ><th scope="row" class="center"...
        while (!line.contains("<th scope=\"row\"")) {
            line = scan.nextLine();
        }
        // Loops Through all rows/players
        while (line.contains("<th scope=\"row\"")) {
            // Recheck so not to lose index for regex
            pattern = Pattern.compile(">([\\p{L},[0-9]-\\.\\(\\) ]+)<");
            matcher = pattern.matcher(line);
            // Add and store current player
            values.add(new ArrayList<String>());
            List<String> currentPlayer = values.get(values.size() - 1);
            // Loop until all info on one player is found
            while (matcher.find()) {
                currentPlayer.add(matcher.group(1).trim()); // Add value
            }
            line = scan.nextLine();
        }
        // Print Out Headers and values to TSV
        for (int i = 0; i < headers.size() - 1; i++) {
            output.print(headers.get(i) + "\t");
        }
        output.println(headers.get(headers.size() - 1));

        for (int i = 0; i < values.size(); i++) {
            List<String> currentPlayer = values.get(i);
            for (int j = 0; j < currentPlayer.size() - 1; j++) {
                output.print(currentPlayer.get(j) + "\t");
            }
            output.println(currentPlayer.get(currentPlayer.size() - 1));
        }
        output.close();
    }

    public static void printAssistantCoachesAndStaffTSV(Scanner scan, PrintStream output) {
        // Lists to store vales for TSV. Each list for values is a player.
        // Pattern and matcher are for regex.
        List<String> headers = new ArrayList<String>();
        List<List<String>> values = new ArrayList<List<String>>();
        Pattern pattern;
        Matcher matcher;
        // Loop until finds section where Staff is
        // eg: ....html'>Jud Buechler</a>...
        String line = scan.nextLine();
        while (!line.contains("<tr><td><a href")) {
            line = scan.nextLine();
        }
        // Loops until table is finished
        while (!line.contains("</table>")) {
            pattern = Pattern.compile("(\\p{L}+[/ &;][\\p{L} ]+)<");
            matcher = pattern.matcher(line);
            while (matcher.find()) {
                String name = matcher.group(1); // Get staff name
                matcher.find(); // Find next Match
                String position = matcher.group(1);
                if (position.contains("nbsp;")) {
                    position = position.replace("nbsp;", "");
                }
                if (!headers.contains(position)) { // Put position into headers
                    headers.add(position);
                    values.add(new ArrayList<String>());
                }
                // Position to add coaches to
                int currentPositionIndex = headers.indexOf(position);
                List<String> currentPosition = values.get(currentPositionIndex);
                currentPosition.add(name);
            }
            line = scan.nextLine();
        }
        // Print Out Headers to TSV
        for (int i = 0; i < headers.size() - 1; i++) {
            output.print(headers.get(i) + "\t");
        }
        output.println(headers.get(headers.size() - 1));
        // Get the position with the most people
        // For formatting the TSV
        int max = 0;
        for (int i = 0; i < values.size(); i++) {
            max = Math.max(max, values.get(i).size());
        }
        // Print the People in correct column
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < values.size(); j++) {
                if (values.get(j).size() > i) { // If Position still has people
                    if (j == values.size() - 1) { // Formating so no extra column
                        output.print(values.get(j).get(i));
                    } else {
                        output.print(values.get(j).get(i) + "\t");
                    }

                }
            }
            output.println();
        }
        output.close();
    }

    public static void printTeamAndOpponentStatsTSV(Scanner scan, PrintStream team, PrintStream opponent) {
        // Lists to store vales for TSV. Each list for values is a player.
        // Temp to store temporary values to be put into headers or values.
        List<String> headers = new ArrayList<String>();
        List<List<String>> values = new ArrayList<List<String>>();
        String temp;
        // Loop until Finding Team and Opponent Stats Header
        // eg: <th aria-label="&nbsp;"...
        String line = scan.nextLine();
        while (!line.contains("<th aria-label=")) {
            line = scan.nextLine();
        }
        // Loop until out of headers
        while (line.contains("<th aria-label=")) {
            temp = getElementWithinTag(line);
            if (temp.equals("&nbsp;")) { // Replace &nbsp with blank
                temp = "";
            }
            headers.add(temp);
            line = scan.nextLine();
        }
        // Loop through all Values and update Values List for Team Values
        fillTeamAndOpponentValues(line, scan, headers, values);

        // Print to team_stats.tsv
        for (int i = 0; i < headers.size() - 1; i++) {
            team.print(headers.get(i) + "\t");
        }
        team.println(headers.get(headers.size() - 1));

        for (int i = 0; i < values.size(); i++) {
            List<String> currentStat = values.get(i);
            for (int j = 0; j < currentStat.size() - 1; j++) {
                team.print(currentStat.get(j) + "\t");
            }
            team.println(currentStat.get(currentStat.size() - 1));
        }

        // Loop through all Values and update Values List for Opponent Values
        values.clear();
        fillTeamAndOpponentValues(line, scan, headers, values);

        // Print to opponent_stats.tsv
        for (int i = 0; i < headers.size() - 1; i++) {
            opponent.print(headers.get(i) + "\t");
        }
        opponent.println(headers.get(headers.size() - 1));

        for (int i = 0; i < values.size(); i++) {
            List<String> currentStat = values.get(i);
            for (int j = 0; j < currentStat.size() - 1; j++) {
                opponent.print(currentStat.get(j) + "\t");
            }
            opponent.println(currentStat.get(currentStat.size() - 1));
        }
        team.close();
        opponent.close();
    }

    public static void fillTeamAndOpponentValues(String line, Scanner scan, List<String> headers,
            List<List<String>> values) {
        // Regex Equations
        Pattern pattern;
        Matcher matcher;
        // Loop until Finding Team and Opponent Stats Values
        // eg: <tr ><th scope="row" class="left"...
        while (!line.contains("data-stat=\"player\"")) {
            line = scan.nextLine();
        }

        // Loop Through All Values for output (Team TSV)
        while (line.contains("</tr>")) {
            pattern = Pattern.compile(">(\\.?[%/[0-9] \\p{L}\\.\\+-]+)<");
            matcher = pattern.matcher(line);
            // Add in values to values array
            values.add(new ArrayList<String>());
            // Add values to ArrayList just created
            List<String> currentStat = values.get(values.size() - 1);
            // Loop through each line of values
            while (matcher.find()) {
                currentStat.add(matcher.group(1).trim());
            }
            // During Expantion year Year/Year will be blank
            // Fills values Array with blank entries
            if (currentStat.size() == 1) {
                for (int i = 1; i < headers.size(); i++) {
                    currentStat.add("");
                }
            }
            // Rows after Team row need a blank in G column
            if (currentStat.size() != headers.size()) {
                currentStat.add(1, "");
                // During 1979-80 3 Point line was introduced so some Year/Year
                // Stats are blank for that year
                if (currentStat.size() != headers.size()) {
                    String[] threePointStats = { "3P", "3PA", "3P%" };
                    for (int i = 0; i < threePointStats.length; i++) {
                        currentStat.add(headers.indexOf(threePointStats[i]), "");
                    }
                }
            }
            line = scan.nextLine();
        }
    }

    public static void printTeamMiscTSV(Scanner scan, PrintStream miscOutput, PrintStream advancedOutput,
            PrintStream offFourOutput, PrintStream defFourOutput, int year) {
        // Lists to store vales for TSV. Each list for values is a player.
        // Temp to store temporary values to be put into headers or values.
        // Pattern and matcher are for regex.
        List<List<String>> headers = new ArrayList<List<String>>();
        List<List<String>> values = new ArrayList<List<String>>();
        String temp;
        Pattern pattern;
        Matcher matcher;
        // Loop until finding data
        // eg: <th aria-label="&nbsp;"...
        String line = scan.nextLine();
        while (!line.contains("<th aria-label=")) {
            line = scan.nextLine();
        }
        while (line.contains("<th aria-label=")) { // Skip through top headers
            line = scan.nextLine();
        }
        while (!line.contains("<th aria-label=")) {
            line = scan.nextLine();
        }
        // Add first header, header for misc stats
        // Takes first 11 headers (misc stats)
        headers.add(new ArrayList<String>());
        for (int i = 0; i < 11; i++) {
            temp = getElementWithinTag(line);
            if (temp.equals("&nbsp;")) {
                temp = "";
            }
            headers.get(headers.size() - 1).add(temp);
            line = scan.nextLine();
        }
        // Add Second header, header for Advanced stats
        // Takes 2 headers (Advanced stats)
        headers.add(new ArrayList<String>());
        for (int i = 0; i < 2; i++) {
            headers.get(headers.size() - 1).add(getElementWithinTag(line));
            line = scan.nextLine();
        }
        // Add third header, header for Offensive Four Factor stats
        // Takes 4 headers (Offensive Four Factor stats)
        headers.add(new ArrayList<String>());
        for (int i = 0; i < 4; i++) {
            headers.get(headers.size() - 1).add(getElementWithinTag(line));
            line = scan.nextLine();
        }
        // Add fourth header, header for Defensive Four Factor stats
        // Takes 4 headers (Defensive Four Factor stats)
        headers.add(new ArrayList<String>());
        for (int i = 0; i < 4; i++) {
            headers.get(headers.size() - 1).add(getElementWithinTag(line));
            line = scan.nextLine();
        }
        // Add to first header, header for misc stats
        // Takes 2 headers for Arena and Attendance (misc stats)
        for (int i = 0; i < 2; i++) {
            headers.get(0).add(getElementWithinTag(line));
            line = scan.nextLine();
        }
        // Loop until finding actual data
        // eg: <tr ><th scope="row"...
        while (!line.contains("data-stat=\"player\"")) {
            line = scan.nextLine();
        }
        // Get all Data for values
        while (line.contains("</tr>")) {
            values.add(new ArrayList<String>());
            pattern = Pattern.compile(">(\\.?[%/[0-9] \\p{L}\\.\\+\\(\\),-]+)<");
            matcher = pattern.matcher(line);
            // Add all data to values
            while (matcher.find()) {
                values.get(values.size() - 1).add(matcher.group(1));
            }
            line = scan.nextLine();
        }
        // Add any blanks if necessary
        year = 2010;
        if (year <= 1978) { // No 3's
            for (int i = 0; i < values.size(); i++) {
                values.get(i).add(12, ""); // Add blank for 3PAr
            }
        }
        int totalSize = 0; // Total Size of all Headeres
        for (int i = 0; i < headers.size(); i++) {
            totalSize += headers.get(i).size();
        }
        if (values.get(0).size() == totalSize - 1) { // Missing Attendance
            for (int i = 0; i < values.size(); i++) {
                values.get(i).add(""); // Add blank for Attendance
            }
        }
        values.get(1).add(21, ""); // Add for blank Lg Rank for Arena
        // Print all Things
        // Print to team_misc.tsv
        // Print Headers
        for (int i = 0; i < headers.get(0).size() - 1; i++) {
            miscOutput.print(headers.get(0).get(i) + "\t");
        }
        miscOutput.println(headers.get(0).get(headers.get(0).size() - 1));
        for (int i = 0; i < values.size(); i++) { // Print each row of table
            // Print Values
            for (int k = 0; k < headers.get(0).size() - 2; k++) { // Compensate for Arena & Attendance
                miscOutput.print(values.get(i).get(k) + "\t");
            }
            miscOutput.print(values.get(i).get(values.get(i).size() - 2) + "\t");
            miscOutput.println(values.get(i).get(values.get(i).size() - 1));
        }
        miscOutput.close();
        // Print to advanced_team_misc.tsv
        // Print Headers
        for (int i = 0; i < headers.get(1).size() - 1; i++) {
            advancedOutput.print(headers.get(1).get(i) + "\t");
        }
        advancedOutput.println(headers.get(1).get(headers.get(1).size() - 1));
        // Print Values
        for (int i = 0; i < values.size(); i++) {
            // Compensate for Arena & Attendance being in other header
            int startIndex = headers.get(0).size() - 2;
            int endIndex = startIndex + headers.get(1).size() - 1;
            for (int j = startIndex; j < endIndex; j++) {
                advancedOutput.print(values.get(i).get(j) + "\t");
            }
            advancedOutput.println(values.get(i).get(endIndex));
        }
        advancedOutput.close();
        // Print to offensive_four_factors_team_misc.tsv
        // Print Headers
        for (int i = 0; i < headers.get(2).size() - 1; i++) {
            offFourOutput.print(headers.get(2).get(i) + "\t");
        }
        offFourOutput.println(headers.get(2).get(headers.get(2).size() - 1));
        // Print Values
        for (int i = 0; i < values.size(); i++) {
            // Compensate for Arena & Attendance
            int startIndex = headers.get(0).size() + headers.get(1).size() - 2;
            int endIndex = startIndex + headers.get(2).size() - 1;
            for (int j = startIndex; j < endIndex; j++) { // Print Values
                offFourOutput.print(values.get(i).get(j) + "\t");
            }
            offFourOutput.println(values.get(i).get(endIndex));
        }
        offFourOutput.close();
        // Print to defensive_four_factors_team_misc.tsv
        // Print Headers
        for (int i = 0; i < headers.get(3).size() - 1; i++) {
            defFourOutput.print(headers.get(3).get(i) + "\t");
        }
        defFourOutput.println(headers.get(3).get(headers.get(3).size() - 1));
        // Print Values
        for (int i = 0; i < values.size(); i++) {
            int startIndex = headers.get(0).size() + headers.get(1).size() + headers.get(2).size() - 2;
            int endIndex = startIndex + headers.get(3).size() - 1;
            for (int j = startIndex; j < endIndex; j++) {
                defFourOutput.print(values.get(i).get(j) + "\t");
            }
            defFourOutput.println(values.get(i).get(endIndex));
        }
        defFourOutput.close();
        scan.nextLine(); // Go to Next Line to not confuse Looping
    }

    public static void printPerGameTSV(Scanner scan, PrintStream output, int year) {
        // Lists to store vales for TSV. Each list for values is a player.
        // Temp to store temporary values to be put into headers or values.
        // Pattern and matcher are for regex.
        List<String> headers = new ArrayList<String>();
        List<List<String>> values = new ArrayList<List<String>>();
        String temp;
        Pattern pattern;
        Matcher matcher;
        // Loop until Finding Team and Opponent Stats Header
        // eg: <th aria-label="Rank"...
        String line = scan.nextLine();
        while (!line.contains("<th aria-label=")) {
            line = scan.nextLine();
        }
        // Get All Headers
        while (line.contains("<th aria-label=")) {
            temp = getElementWithinTag(line);
            if (temp.equals("&nbsp;")) {
                temp = "";
            }
            headers.add(temp);
            line = scan.nextLine();
        }
        // Remove blank before ORtg for Per 100
        if (headers.indexOf("Offensive Rating") != -1) {
            headers.remove(headers.indexOf("Offensive Rating") - 1);
        }
        // Loop until finding values
        // eg: <tr ><th scope=...data-stat="ranker"...
        while (!line.contains("data-stat=\"player\"")) {
            line = scan.nextLine();
        }
        // Get all values
        while (line.contains("data-stat=\"player\"")) {
            pattern = Pattern.compile(">(\\.?[%/[0-9]' \\p{L}\\.\\+-]+)<");
            matcher = pattern.matcher(line);
            // Add in values to values array
            values.add(new ArrayList<String>());
            // Add values to ArrayList just created
            List<String> currentPlayer = values.get(values.size() - 1);
            // Loop through each line of values
            while (matcher.find()) {
                currentPlayer.add(matcher.group(1).trim());
            }
            // Blank for GS
            year = 2010;
            if (year <= 1980) { // No GS collected
                currentPlayer.add(4, "");
            }
            // If there are blank values that need to be added
            if (currentPlayer.size() != headers.size()) {
                // if taken shots and attempted shots are 0 then % should be blank
                int shotsTakenIndex = headers.indexOf("FG");
                int shotsAttemptedIndex = headers.indexOf("FGA");
                double shotsTaken = Double.parseDouble(currentPlayer.get(shotsTakenIndex));
                double shotsAttempted = Double.parseDouble(currentPlayer.get(shotsAttemptedIndex));
                if (shotsTaken == 0 && shotsAttempted == 0) {
                    currentPlayer.add(headers.indexOf("FG%"), "");
                }
                if (year > 1978) { // No 3's before 1979
                    // 3's
                    shotsTakenIndex = headers.indexOf("3P");
                    shotsAttemptedIndex = headers.indexOf("3PA");
                    shotsTaken = Double.parseDouble(currentPlayer.get(shotsTakenIndex));
                    shotsAttempted = Double.parseDouble(currentPlayer.get(shotsAttemptedIndex));
                    if (shotsTaken == 0 && shotsAttempted == 0) {
                        currentPlayer.add(headers.indexOf("3P%"), "");
                    }
                    // 2's
                    shotsTakenIndex = headers.indexOf("2P");
                    shotsAttemptedIndex = headers.indexOf("2PA");
                    shotsTaken = Double.parseDouble(currentPlayer.get(shotsTakenIndex));
                    shotsAttempted = Double.parseDouble(currentPlayer.get(shotsAttemptedIndex));
                    if (shotsTaken == 0 && shotsAttempted == 0) {
                        currentPlayer.add(headers.indexOf("2P%"), "");
                    }
                }
                if (currentPlayer.get(headers.indexOf("FG%")).equals("")) {
                    // Blank for eFG%
                    currentPlayer.add(headers.indexOf("Effective Field Goal Percentage"), "");
                }
                // Free Throws
                shotsTakenIndex = headers.indexOf("FT");
                shotsAttemptedIndex = headers.indexOf("FTA");
                shotsTaken = Double.parseDouble(currentPlayer.get(shotsTakenIndex));
                shotsAttempted = Double.parseDouble(currentPlayer.get(shotsAttemptedIndex));
                if (shotsTaken == 0 && shotsAttempted == 0) {
                    currentPlayer.add(headers.indexOf("FT%"), "");
                }
            }
            line = scan.nextLine();
        }
        line = scan.nextLine(); // Go to next line to not confuse looping

        // Print Out Headers and values to TSV
        for (int i = 0; i < headers.size() - 1; i++) {
            output.print(headers.get(i) + "\t");
        }
        output.println(headers.get(headers.size() - 1));

        for (int i = 0; i < values.size(); i++) {
            List<String> currentPlayer = values.get(i);
            for (int j = 0; j < currentPlayer.size() - 1; j++) {
                output.print(currentPlayer.get(j) + "\t");
            }
            output.println(currentPlayer.get(currentPlayer.size() - 1));
        }
        output.close();
    }

    // Prints Totals, same format as Per Game
    public static void printTotalsTSV(Scanner scan, PrintStream output, int year) {
        printPerGameTSV(scan, output, year);
    }

    // Prints Per 36 Minutes, same format as Per Game
    public static void printPer36TSV(Scanner scan, PrintStream output, int year) {
        printPerGameTSV(scan, output, year);
    }

    // Prints Per 100 Possessions, same format as Per Game
    public static void printPer100TSV(Scanner scan, PrintStream output, int year) {
        printPerGameTSV(scan, output, year);
    }

    public static void printPerGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay(
            String perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay, Scanner scan,
            PrintStream output, int year) {
        if (!perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay.equals("SHOOTING")
                && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay.equals("PLAY BY PLAY")) {
            output.println(perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay);
        }
        output.println();
        int amountOfStats = 0;
        if (!perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay.equals("PER 100 POSSESSIONS")
                && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                        .equals("PLAYOFFS PER 100 POSSESSIONS")) {
            amountOfStats = 23;
        } else {
            amountOfStats = 26;
        }
        String line = scan.nextLine();

        while (!line.contains("th aria-label=\"Rank\" data-stat=\"ranker\"")) {
            line = scan.nextLine();
        }

        while (!line.contains("</tr>")) {
            output.print("|");
            line = bracketBreaker(line);
            output.print(line); // Print out "Rk" Header
            for (int j = 0; j < 3 - line.length(); j++) {
                output.print(" ");
            }
            output.print("|");

            line = scan.nextLine();
            output.print("Name                    "); // Print out "Name" Header
            output.print("|");

            for (int j = 0; j < 2; j++) {
                line = scan.nextLine();
                headerMaker(3, line, 1, output); // Print out "Age", "G" headers
            }

            // Prints out Header

            if (!perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay.equals("ADVANCED")
                    && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                            .equals("PLAYOFFS ADVANCED")
                    && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay.equals("SHOOTING")
                    && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                            .equals("PLAY BY PLAY")) { // Print headers for all tables but advanced
                line = scan.nextLine();
                headerMaker(3, line, 1, output); // Print out "GS" Header
                for (int j = 0; j < amountOfStats; j++) { // Print out rest of stats
                    if (j >= 4 && j <= 10) { // Adjust for no 3's before 1979
                        if (year >= 1979) {
                            if (j == 10
                                    && (!perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                            .equals("PER 36 MINUTES")
                                            && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                                    .equals("PER 100 POSSESSIONS"))
                                    && j == 10
                                    && (!perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                            .equals("PLAYOFFS PER 36 MINUTES")
                                            && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                                    .equals("PLAYOFFS PER 100 POSSESSIONS"))) { // Effective Field Goal
                                                                                                // Stat
                                line = scan.nextLine();
                                headerMaker(5, line, 4, output);
                            } else if (j != 10) {
                                line = scan.nextLine();
                                headerMaker(5, line, 1, output);
                            }
                        }
                    } else if (j == 20) { // No TOV in 1976
                        if (year != 1976) {
                            line = scan.nextLine();
                            headerMaker(5, line, 1, output);
                        }
                    } else if (j == 23) { // Empty Space
                        if (perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                .equals("PER 100 POSSESSIONS")
                                || perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                        .equals("PLAYOFFS PER 100 POSSESSIONS")) {
                            line = scan.nextLine();
                            headerMaker(1, line, 1, output);
                        }
                    } else if (j >= 24) { // Offensive Rating and Defensive Rating
                        if (perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                .equals("PER 100 POSSESSIONS")
                                || perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                        .equals("PLAYOFFS PER 100 POSSESSIONS")) {
                            line = scan.nextLine();
                            headerMaker(5, line, 4, output);
                        }
                    } else { // Rest of stats
                        line = scan.nextLine();
                        headerMaker(5, line, 1, output);
                    }
                }
            } else if (perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay.equals("ADVANCED")
                    || perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                            .equals("PLAYOFFS ADVANCED")) { // Advanced Table
                int amountOfAdvanedStats = 0;
                if (year == 1976) { // No TOV or USG in 1976
                    amountOfAdvanedStats = 9;
                } else if (year <= 1978) { // No 3 pointer until 1979
                    amountOfAdvanedStats = 11;
                } else {
                    amountOfAdvanedStats = 12;
                }
                line = scan.nextLine();
                headerMaker(5, line, 1, output); // Print out MP

                for (int i = 0; i < amountOfAdvanedStats; i++) {
                    line = scan.nextLine();
                    headerMaker(5, line, 4, output);
                }
                line = scan.nextLine();
                output.print(" |"); // Empty Space
                for (int i = 0; i < 3; i++) { // Print out "OWS", "DWS", "WS" Headers
                    line = scan.nextLine();
                    headerMaker(5, line, 4, output);
                }
                line = scan.nextLine();
                headerMaker(7, line, 4, output); // "WS/48" Header
                line = scan.nextLine();
                output.print(" |"); // Empty Space
                for (int i = 0; i < 3; i++) { // Print out "OBPM", "DBPM", "BPM" Headers
                    line = scan.nextLine();
                    headerMaker(5, line, 4, output);
                }
                line = scan.nextLine();
                headerMaker(5, line, 6, output); // Print out VORP Header
            } else if (perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay.equals("SHOOTING")) { // Shooting
                for (int i = 0; i < 23; i++) { // Print headers
                    if (i <= 14) { // Print all headers
                        if (i == 7) { // Print "16 <3" Header
                            line = scan.nextLine();
                            line = customBracketBreaker(line, 2);
                            int bracket1 = line.indexOf("<", 4);
                            output.print(line.substring(0, bracket1));
                            for (int j = 0; j < 5 - line.substring(0, bracket1).length(); j++) {
                                output.print(" ");
                            }
                            output.print("|");
                        } else if (i == 13) { // Print "16 <3" Header
                            line = scan.nextLine();
                            output.print("16 <3");
                            output.print("|");
                        } else {
                            line = scan.nextLine();
                            headerMaker(5, line, 1, output);
                        }
                    } else if (i >= 15 && i <= 16) { // Print out "%Ast'd" and "%FGA" Headers
                        line = scan.nextLine();
                        headerMaker(6, line, 1, output);
                    } else if (i == 17) { // Prints Made Dunks
                        line = scan.nextLine();
                        headerMaker(4, line, 2, output);
                    } else if (i >= 18 && i <= 20) { // Print out "%Ast'd", "%3PA", "3P%" Headers
                        line = scan.nextLine();
                        headerMaker(6, line, 1, output);
                    } else { // Prints out "Att." and "Md." Heaves
                        line = scan.nextLine();
                        headerMaker(3, line, 1, output);
                    }
                }
            } else { // Play by Play
                for (int i = 0; i < 17; i++) { // Print headers
                    if (i <= 5) { // Headers up to OnCourt
                        line = scan.nextLine();
                        headerMaker(5, line, 1, output);
                    } else if (i >= 6 && i <= 9) { // Print out "OnCourt", "On-Off", "BadPass", "LostBall", "Shoot", and
                                                   // "Off." Headers
                        line = scan.nextLine();
                        headerMaker(8, line, 1, output);
                    } else if (i >= 10 && i <= 13) { // Print out "Shoot" and "Off." Headers
                        line = scan.nextLine();
                        headerMaker(7, line, 1, output);
                    } else if (i == 14) { // Print out "PGA" Headers
                        line = scan.nextLine();
                        headerMaker(5, line, 4, output);
                    } else { // "And1", and "Blkd" Headers
                        line = scan.nextLine();
                        headerMaker(5, line, 1, output);
                    }
                }
            }
            line = scan.nextLine();
            output.println();
        }

        line = scan.nextLine();
        while (!line.contains("data-stat=\"ranker\"")) {
            line = scan.nextLine();
        }

        // Print out Player Statistics
        while (line.length() > 0) {
            output.print("|");
            line = statMaker(line, 3, 2, output); // Print out Rk
            line = statMaker(line, 24, 3, output); // Print out Name
            line = statMaker(line, 3, 3, output); // Print out Age
            if (perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay.equals("SHOOTING")
                    || perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                            .equals("PLAY BY PLAY")) {
                line = statMaker(line, 3, 2, output); // Print out Games
            } else {
                if (year <= 1982) {
                    line = statMaker(line, 3, 2, output); // Print out Games
                } else {
                    line = statMaker(line, 3, 3, output); // Print out Games
                }
            }
            if (!perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay.equals("ADVANCED")
                    && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                            .equals("PLAYOFFS ADVANCED")
                    && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay.equals("SHOOTING")
                    && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                            .equals("PLAY BY PLAY")) { // No Games Started in Advanced
                if (year <= 1982) {
                    line = statMaker(line, 3, 2, output); // Print out Games Started
                } else {
                    line = statMaker(line, 3, 3, output); // Print out Games Started
                }
            }
            if (!perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay.equals("ADVANCED")
                    && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                            .equals("PLAYOFFS ADVANCED")
                    && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay.equals("SHOOTING")
                    && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                            .equals("PLAY BY PLAY")) { // Print headers for all tables but advanced
                for (int j = 0; j < amountOfStats; j++) { // Print out rest of stats
                    if (j >= 4 && j <= 10) { // Adjust for no 3's before 1979
                        if (year >= 1979) {
                            if ((j == 10
                                    && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                            .equals("PER 36 MINUTES")
                                    && j == 10
                                    && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                            .equals("PER 100 POSSESSIONS"))
                                    && (j == 10
                                            && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                                    .equals("PLAYOFFS PER 36 MINUTES")
                                            && j == 10
                                            && !perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                                    .equals("PLAYOFFS PER 100 POSSESSIONS"))) { // Per 36 and Per 100
                                                                                                // does not measure eFG%
                                line = statMaker(line, 5, 2, output);
                            } else if (j != 10) {
                                line = statMaker(line, 5, 2, output);
                            }
                        }
                    } else if (j == 22) {
                        if (year >= 1977) {
                            line = statMaker(line, 5, 2, output);
                        }
                    } else if (j == 23) {
                        if (perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                .equals("PER 36 MINUTES")
                                || perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                        .equals("PER 100 POSSESSIONS")
                                || perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                        .equals("PLAYOFFS PER 36 MINUTES")
                                || perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                        .equals("PLAYOFFS PER 100 POSSESSIONS")) {
                            line = statMaker(line, 1, 2, output);
                        }
                    } else if (j >= 24) { // Offensive Rating and Defensive Rating
                        if (perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                .equals("PER 36 MINUTES")
                                || perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                        .equals("PER 100 POSSESSIONS")
                                || perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                        .equals("PLAYOFFS PER 36 MINUTES")
                                || perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                                        .equals("PLAYOFFS PER 100 POSSESSIONS")) {
                            line = statMaker(line, 5, 2, output);
                        }
                    } else { // Rest of stats
                        line = statMaker(line, 5, 2, output);
                    }
                }
            } else if (perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay.equals("ADVANCED")
                    || perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay
                            .equals("PLAYOFFS ADVANCED")) { // Advanced Table
                int amountOfAdvanedStats = 0;
                if (year == 1976) { // No TOV or USG in 1976
                    amountOfAdvanedStats = 9;
                } else if (year <= 1978) { // No 3 pointer until 1979
                    amountOfAdvanedStats = 11;
                } else {
                    amountOfAdvanedStats = 12;
                }
                if (year >= 1983) {
                    line = statMaker(line, 5, 3, output); // Print out MP
                } else {
                    line = statMaker(line, 5, 2, output); // Print out MP
                }
                for (int i = 0; i < amountOfAdvanedStats; i++) {
                    line = statMaker(line, 5, 2, output);
                }
                line = statMaker(line, 1, 2, output); // Empty Space
                for (int i = 0; i < 3; i++) { // Print out "OWS", "DWS", "WS" Headers
                    line = statMaker(line, 5, 2, output);
                }
                line = statMaker(line, 7, 2, output); // "WS/48" Header
                line = statMaker(line, 1, 2, output); // Empty Space
                for (int i = 0; i < 4; i++) { // Print out "OBPM", "DBPM", "BPM", and "VORP" Headers
                    line = statMaker(line, 5, 2, output);
                }
            } else if (perGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay.equals("SHOOTING")) { // Shooting
                for (int i = 0; i < 23; i++) { // Print headers
                    if (i <= 14) { // Print all headers
                        line = statMaker(line, 5, 2, output);
                    } else if (i >= 15 && i <= 16) { // Print out "%Ast'd" and "%FGA" Headers
                        line = statMaker(line, 6, 2, output);
                    } else if (i == 17) { // Prints Made Dunks
                        line = statMaker(line, 4, 2, output);
                    } else if (i >= 18 && i <= 20) { // Print out "%Ast'd", "%3PA", "3P%" Headers
                        line = statMaker(line, 6, 2, output);
                    } else if (i == 21) { // Print out "Att." Heaves
                        line = statMaker(line, 4, 2, output);
                    } else { // Print out "Md." Heaves
                        line = statMaker(line, 3, 2, output);
                    }
                }
            } else { // Play By Play
                for (int i = 0; i < 17; i++) { // Print headers
                    if (i <= 5) { // Headers up to OnCourt
                        line = statMaker(line, 5, 2, output);
                    } else if (i >= 6 && i <= 7) { // Print out "OnCourt" and "On-Off" Headers
                        line = statMaker(line, 8, 2, output);
                    } else if (i >= 8 && i <= 9) { // Print out "BadPass", "LostBall", "Shoot", "Off." Headers
                        line = statMaker(line, 8, 2, output);
                    } else if (i >= 10 && i <= 13) { // Print out "Shoot" and "Off." Headers
                        line = statMaker(line, 7, 2, output);
                    } else { // Print out "PGA", "And1", and "Blkd" Headers
                        line = statMaker(line, 5, 2, output);
                    }
                }
            }
            line = scan.nextLine();
            output.println();
        }
    }

    public static String bracketBreaker(String line) {
        int bracket2 = line.indexOf(">");
        line = line.substring(bracket2 + 1);
        int bracket1 = line.indexOf("<");
        line = line.substring(0, bracket1);
        return line;
    }

    public static String getElementWithinTag(String line) {
        int bracket2 = line.indexOf(">");
        line = line.substring(bracket2 + 1);
        int bracket1 = line.indexOf("<");
        line = line.substring(0, bracket1);
        return line;
    }

    public static String doubleBracketBreaker(String line) {
        int bracket2 = line.indexOf(">");
        line = line.substring(bracket2 + 1);
        bracket2 = line.indexOf(">");
        line = line.substring(bracket2 + 1);
        return line;
    }

    public static String customBracketBreaker(String line, int num) {
        for (int i = 0; i < num; i++) {
            int index = line.indexOf(">");
            line = line.substring(index + 1);
        }
        return line;
    }
}
