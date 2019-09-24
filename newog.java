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

                // Make all necessary Paths
                Path path = Paths.get(
                        ".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\team_and_opponent_stats");
                // Checks if directories exist
                if (!Files.exists(path)) {
                    try {
                        Files.createDirectories(path);
                    } catch (IOException e) {
                        // Fail to create directories
                        e.printStackTrace();
                    }
                }
                path = Paths.get(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\team_misc");
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

                PrintStream output = new PrintStream(f);

                URL u = new URL("https://www.basketball-reference.com/teams/" + team + "/" + (year + 1) + ".html");

                // URL u = new URL("https://www.basketball-reference.com/teams/" + "GSW" + "/" +
                // "2019" + ".html");

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
                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear
                        + "\\team_and_opponent_stats" + "\\team_stats.tsv");
                output = new PrintStream(f);
                // New TSV file for Opponent Stats
                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear
                        + "\\team_and_opponent_stats" + "\\opponent_stats.tsv");

                // Make TSV of The Team and Opponent Stats Table
                // Team and Opponent Stats stores in seperate TSV files
                printTeamAndOpponentStatsTSV(scan, output, new PrintStream(f));

                // New TSV File for Team Misc Stats
                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\team_misc"
                        + "\\team_misc.tsv");
                output = new PrintStream(f);

                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\team_misc"
                        + "\\advanced_team_misc.tsv");
                PrintStream output2 = new PrintStream(f);

                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\team_misc"
                        + "\\offensive_four_factors_team_misc.tsv");
                PrintStream output3 = new PrintStream(f);

                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\team_misc"
                        + "\\defensive_four_factors_team_misc.tsv");

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

                f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\" + "advanced.tsv");
                output = new PrintStream(f);

                // Make TSV for Advanced
                printAdvancedTSV(scan, output, year);

                // Loop until Shooting, playoffs, salaries, or end of file
                while (!line.contains("all_shooting") && !line.contains("all_playoffs_totals")
                        && !line.contains("\"all_salaries2\"") && !line.contains("<!-- global.nonempty_tables_num:")) {
                    line = scan.nextLine();
                }

                if (line.contains("all_shooting")) { // Shooting and Play by Play available as of 2000-2001 season
                    line = scan.nextLine();
                    // printShootingHeader(output);

                    // printPerGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay("SHOOTING",
                    // scan,
                    // output, year);

                    // output.println();

                    // while (!line.contains("<tr>")) {
                    // line = scan.nextLine();
                    // }

                    // printPlayByPlayHeader(output);

                    // printPerGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay("PLAY
                    // BY PLAY", scan,
                    // output, year);

                    // line = scan.nextLine();

                    // output.println();

                }

                while (!line.contains("all_shooting") && !line.contains("all_playoffs_totals")
                        && !line.contains("\"all_salaries2\"") && !line.contains("<!-- global.nonempty_tables_num:")) {
                    line = scan.nextLine();
                }

                if (line.contains("all_playoffs_totals")) { // Print Playoff Stats
                    // Print playoff totals
                    f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\"
                            + "playoff_totals.tsv");
                    output = new PrintStream(f);

                    printTotalsTSV(scan, output, year);

                    // Pring Playoff Per Game
                    f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\"
                            + "playoff_per_game.tsv");
                    output = new PrintStream(f);

                    printPerGameTSV(scan, output, year);

                    // Print Per 36
                    f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\"
                            + "playoff_per_36.tsv");
                    output = new PrintStream(f);

                    printPer36TSV(scan, output, year);

                    // Print Per 100
                    f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\"
                            + "playoff_per_100.tsv");
                    output = new PrintStream(f);

                    printPer100TSV(scan, output, year);

                    // Print Advanced
                    f = new File(".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\"
                            + "playoff_advanced.tsv");
                    output = new PrintStream(f);

                    printAdvancedTSV(scan, output, year);

                    // if (year >= 2000) { // Shooting and Play by Play available as of 2000-2001
                    // season

                    // output.print("PLAYOFFS ");

                    // // printShootingHeader(output);

                    // //
                    // printPerGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay("SHOOTING",
                    // // scan,
                    // // output, year);

                    // output.println();

                    // while (!line.contains("<tr>")) {
                    // line = scan.nextLine();
                    // }

                    // output.print("PLAYOFFS ");

                    // // printPlayByPlayHeader(output);

                    // //
                    // printPerGameOrTotalsOrPer36OrPer100OrAdvancedOrPlayoffsOrShootingOrPlayByPlay("PLAY
                    // // BY PLAY",
                    // // scan, output, year);

                    // output.println();

                    // }
                }

                while (!line.contains("\"all_salaries2\"") && !line.contains("<!-- global.nonempty_tables_num:")) {
                    line = scan.nextLine();
                }

                if (line.contains("\"all_salaries2\"")) {
                    while (!line
                            .contains("<div id=\"all_salaries2\" class=\"table_wrapper setup_commented commented\">")) {
                        line = scan.nextLine();
                    }

                    // Print Salaries if available
                    f = new File(
                            ".\\Every NBA Team Stat\\" + team + "\\" + year + "-" + nextYear + "\\" + "salaries.tsv");
                    output = new PrintStream(f);

                    printSalaryTSV(scan, output);
                }
                year++;
            }
        }
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
        if (line.contains("Executive")) {
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
        }
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
            pattern = Pattern.compile("([\\p{L}'-\\.]+[/ -&;][\\p{L}- \\.']+)<");
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
                    if (currentPlayer.get(headers.indexOf("FG%")).equals("")) {
                        // Blank for eFG%
                        currentPlayer.add(headers.indexOf("Effective Field Goal Percentage"), "");
                    }
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
        output.close();
    }

    // Prints Per 36 Minutes, slightly different from Per Game
    public static void printPer36TSV(Scanner scan, PrintStream output, int year) {
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
            if (year <= 1980) { // No GS collected
                currentPlayer.add(4, "");
            }
            // Add blanks for players like JamesOn Curry with less than 1 minute played
            int minutesPlayed = headers.indexOf("MP");
            if (currentPlayer.get(minutesPlayed).equals("0") && currentPlayer.size() == minutesPlayed + 1) {
                for (int i = currentPlayer.size(); i < headers.size(); i++) {
                    currentPlayer.add("");
                }
            }
            // If there are blank values that need to be added
            // Slightly different from perGame since sometimes both 0's still have %
            if (currentPlayer.size() != headers.size()) {
                // if taken shots and attempted shots are 0 then % should be blank
                int shotsTakenIndex = headers.indexOf("FG");
                int shotsAttemptedIndex = headers.indexOf("FGA");
                String shotsTaken = currentPlayer.get(shotsTakenIndex);
                String shotsAttempted = currentPlayer.get(shotsAttemptedIndex);
                if (shotsTaken.equals("0.0") && shotsAttempted.equals("0.0")) {
                    currentPlayer.add(headers.indexOf("FG%"), "");
                }
                if (year > 1978) { // No 3's before 1979
                    // 3's
                    shotsTakenIndex = headers.indexOf("3P");
                    shotsAttemptedIndex = headers.indexOf("3PA");
                    shotsTaken = currentPlayer.get(shotsTakenIndex);
                    shotsAttempted = currentPlayer.get(shotsAttemptedIndex);
                    if (shotsTaken.equals("0.0") && shotsAttempted.equals("0.0")) {
                        currentPlayer.add(headers.indexOf("3P%"), "");
                    }
                    // 2's
                    shotsTakenIndex = headers.indexOf("2P");
                    shotsAttemptedIndex = headers.indexOf("2PA");
                    shotsTaken = currentPlayer.get(shotsTakenIndex);
                    shotsAttempted = currentPlayer.get(shotsAttemptedIndex);
                    if (shotsTaken.equals("0.0") && shotsAttempted.equals("0.0")) {
                        currentPlayer.add(headers.indexOf("2P%"), "");
                    }
                }
                // Free Throws
                shotsTakenIndex = headers.indexOf("FT");
                shotsAttemptedIndex = headers.indexOf("FTA");
                shotsTaken = currentPlayer.get(shotsTakenIndex);
                shotsAttempted = currentPlayer.get(shotsAttemptedIndex);
                if (shotsTaken.equals("0.0") && shotsAttempted.equals("0.0")) {
                    currentPlayer.add(headers.indexOf("FT%"), "");
                }
            }
            line = scan.nextLine();
        }
        scan.nextLine();
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

    // Prints Per 100 Possessions, same format as Per 36
    public static void printPer100TSV(Scanner scan, PrintStream output, int year) {
        printPer36TSV(scan, output, year);
        output.close();
    }

    // Print Advanced, Slightly different from Per 36
    public static void printAdvancedTSV(Scanner scan, PrintStream output, int year) {
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
        if (headers.indexOf("Offensive Win Shares") != -1) {
            headers.remove(headers.indexOf("Offensive Win Shares") - 1);
        }
        if (headers.indexOf("Offensive Box Plus/Minus") != -1) {
            headers.remove(headers.indexOf("Offensive Box Plus/Minus") - 1);
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
            // Add blanks for players like JamesOn Curry with less than 1 minute played
            int minutesPlayed = headers.indexOf("MP");
            if (currentPlayer.get(minutesPlayed).equals("0") && currentPlayer.size() == 12) {
                for (int i = currentPlayer.size(); i < headers.size() - 1; i++) {
                    currentPlayer.add(minutesPlayed + 1, "");
                }
                currentPlayer.add(headers.indexOf("Win Shares Per 48 Minutes"), "");
            }
            line = scan.nextLine();
        }
        scan.nextLine();
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

    // Prints Out Salary if Available
    public static void printSalaryTSV(Scanner scan, PrintStream output) {
        // Lists to store vales for TSV. Each list for values is a player.
        // Temp to store temporary values to be put into headers or values.
        // Pattern and matcher are for regex.
        List<String> headers = new ArrayList<String>();
        List<List<String>> values = new ArrayList<List<String>>();
        String temp;
        Pattern pattern;
        Matcher matcher;
        // Loop to first Header
        // eg: <th aria-label="Rank" data-stat="ranker"
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
        // Go to Values
        // eg: <tr ><th scope="row" class="center " data-stat="ranker"
        while (!line.contains("data-stat=\"ranker\"")) {
            line = scan.nextLine();
        }
        // Get all Values
        while (line.contains("data-stat=\"ranker\"")) {
            pattern = Pattern.compile(">\\$?([,/[0-9]' \\p{L}]+)<");
            matcher = pattern.matcher(line);
            // Add in values to values array
            values.add(new ArrayList<String>());
            // Add values to ArrayList just created
            List<String> currentPlayer = values.get(values.size() - 1);
            // Loop through each line of values
            while (matcher.find()) {
                currentPlayer.add(matcher.group(1).trim().replace(",", ""));
            }
            line = scan.nextLine();
        }
        scan.nextLine();
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

    public static String getElementWithinTag(String line) {
        int bracket2 = line.indexOf(">");
        line = line.substring(bracket2 + 1);
        int bracket1 = line.indexOf("<");
        line = line.substring(0, bracket1);
        return line;
    }

}
