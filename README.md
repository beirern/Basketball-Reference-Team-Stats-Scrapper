# Basketball-Reference-Team-Stats-Scrapper
Gets the Team Stats from basketballreference.com/teams/[TEAM]/[YEAR]

## Excuse me?
Ok, buckle in, this one is a doozy. If you are a basketball fan you probably know of a website called [basketball reference](basketballreference.com).
It has a lot of stats for any player of any year in the NBA. This takes every table from basketballreference.com/teams/[TEAM]/[YEAR] and makes it into
a csv.

## Why did you do this man
When I had finished my first college Java course the teacher showed a small project he did with the skills he taught us in the class
to show that even with the basic knowledge we learned and some Googling we could do something cool. He basically used the City of Seattle's
crime stats website and built a Java app that made querying the data easier. I was so insipired that over the winter break of 2018 I decided
to scrap every team stats page from basketball reference starting from 1976 (When NBA was official after ABA and NBA merger). I chose the
team stats because kaggle already has individual [player stats](https://www.kaggle.com/drgilermo/nba-players-stats).

## How does it work
It really doesn't. I came back to this to make this README and at this time it doesn't work. The way I built this is terrible. I used Java because
it was the only language I knew at the time. I downloaded the HTML of every single team's website and then parse through the entire HTML
using Regex to get to each table and get each stat. Extremely slow to say the least...

## So you never got it to work!
Slow down Scrooge! I did get it to work when I initally wrote it! Here it is: [My Dataset](https://www.kaggle.com/nick127/basketball-reference-team-page-stats).
It has a whopping 3.5 usability but it is all there! Each team has it's own folder (This is sensitive to name/location changes) and within
each folder there are folders based on the year. Then there are csv files and folders which contain csv files with all the tables, you can
probably see why no one has used this dataset yet.
