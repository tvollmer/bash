#!/bin/bash

#Version 1.0

### Set globals
ALARM_EXE=/usr/local/bin/alarm
HOSTNAME=$(hostname)
THISSCRIPT=${BASH_SOURCE[1]}
#DEBUG=1

### See bottom for init functions

debug()
{
if [ "${DEBUG}" = "1" ]
then
        logger "Debug - ${JOBID} ${THISSCRIPT}: ${1}"
        echo "Debug: ${1}"
else
        logger "Debug - ${JOBID} ${THISSCRIPT}: ${1}"
fi
}


canirun()
{
# Is this job already running?
LOCKFILE="/tmp/${JOBID}.pid"
if [ -e ${LOCKFILE} ]
then
	TMPPID=$(cat ${LOCKFILE})
	if [ $( ps -p ${TMPPID} | grep ${TMPPID} |  wc -l  | awk '{print$1}') = "1" ]
	then
		OUTPUT="Script already running, aborting overrun"
		ABORT=1
		mail_error
	else
		OUTPUT="Removing dead lockfile"
		mail_warning
		rm ${LOCKFILE}
	fi
fi

echo $$ > ${LOCKFILE}		
}

tmpfile()
{
#Generate a temp file. Add it to the TMPFILES array. Return in var named in arg1

T=$(/bin/mktemp -t tmpfile-${JOBID}-XXXXXXXXXXX)
TMPFILES="${T} ${TMPFILES}"
eval "${1}=${T}"
debug "Creating temp file: ${1} is ${T}"
}

alarm_catch()
{
# Clear the handler, send an email, exit

trap - SIGALRM
OUTPUT="Timeout Alarm caught running job ${JOBID} : ${PROGRAM} in ${THISSCRIPT}"
mail_error
exit
}

cleanup()
{
# If we're ABORTING, just drop the bomb on myself

if [ "$ABORT" = "1" ]
then
	kill -9 $$
fi

# Otherwise, be nice as script ends or was cancelled
debug "Running cleanup"

# cleanup any temp files
if [ "${TMPFILES}" ]
then
	for TMPFILE in ${TMPFILES}
	do
		if [ -e ${TMPFILE} ]
		then
			debug "Removing temp file: ${TMPFILE}"
			rm ${TMPFILE}
		fi
	done
fi

# Finally, remove our lockfile
rm ${LOCKFILE}
TIME=$(date)
logger "Script ${JOBID} ended at ${TIME}"
}

mail_warning()
{
# Send email about a non-fatal condition

echo "${OUTPUT}" | mail ${MAIL_TO} ITUNIXADMIN@SUDDENLINK.COM -s "ScriptWarning: ${HOSTNAME} ${JOBID} ${THISSCRIPT}"
}

mail_error()
{
# Send email on error with the output of the last executed command

echo "${OUTPUT}" | mail ${MAIL_TO} ITUNIXADMIN@SUDDENLINK.COM -s "ScriptError: ${HOSTNAME} ${JOBID} ${THISSCRIPT}"
exit
}

alarm_set()
{
# set timeout alarm to [arg1] seconds

if [ "$1" = "" ]
then
	TIMER=30
else
	TIMER=$1
fi
trap alarm_catch SIGALRM
ALARM_PID=$(${ALARM_EXE} ${TIMER})
}

alarm_clear()
{
# Clear existing alarm handler

kill ${ALARM_PID}
trap - SIGALRM
}

alarm_run_checking()
{
# Run command with a timer, checking result code

PROGRAM=$1
TIMER=$2
alarm_set "${TIMER}"
run_checking "${PROGRAM}"
alarm_clear
}

alarm_run()
{
# Run command with a timer, ignore result code

PROGRAM=$1
TIMER=$2
alarm_set "${TIMER}"
run "${PROGRAM}"
alarm_clear
}

run_checking()
{
# Run command, error out if result is bad
PROGRAM=$1
run "${PROGRAM}"
if [ $LASTRES != 0 ]
then
	OUTPUT="Return code: $? running ${PROGRAM}: ${OUTPUT}"
	mail_error
fi
}

run()
{
# Run command, return result code in LASTRES
PROGRAM=$1
debug "Running: ${PROGRAM}"
CMDLINE="${PROGRAM} 2>&1"
debug "CMDLINE is ${CMDLINE}"
OUTPUT=$(eval $CMDLINE)
LASTRES=$?
}

### Startup initialization functions

if [ "${JOBID}" = "" ]
then
	echo "Jobs cannot run without a JOB ID"
	exit
fi

# Set an exit trap to force cleanup
trap cleanup 0

#Are we allowed to run?
canirun

# Log the start fo this script
logger "Script ${JOBID} started at ${TIME}"

