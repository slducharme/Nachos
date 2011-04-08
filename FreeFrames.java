package nachos.userprog;

import nachos.machine.*;
import nachos.threads.*;
import nachos.userprog.*;

public class FreeFrames
{
	FreeFrames()
	{
		private int startPos
		private int numFrames

		/**
		 * Constructor will initialize start position and number of frames free.
		 */
		FreeFrames(int start, int frames)
		{
			startPos = start
			numFrames = frames
		}
	
		/**
		 * Returns the start position for a section of frames
		 */
		int getStart()
		{
			return startPos
		}	

		/**
		 * Returns the number of frames in a given section of frames
		 */
		int getFrames()
		{
			return numFrames
		}
		
		/**
		 * Returns the start position for the next section of potentially free frames added to the list
		 */
		int getStartPosForNext()
		{
			return startPos + numFrames + 1
		}
	}
}