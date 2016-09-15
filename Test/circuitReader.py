import requests
import sys

f = open(sys.argv[1],'r')

baseURL = "http://sampang.internet-box.ch:8080/run.php?uid=1"

requests.get(baseURL+'&start')
print "run started"
time = 1

for line in f:
	separatedLine = str.split(line,',')
	y = separatedLine[0]
	x = separatedLine[1]
	requests.get(baseURL+'&x='+str(x)+'&y='+str(y)+'&cnt='+str(time)+'&time='+str(time))
	time = time + 1
	print "line sent, x="+str(x)+" and y="+str(y)

requests.get(baseURL+'&time='+str(time)+'&end')
print "run done"
