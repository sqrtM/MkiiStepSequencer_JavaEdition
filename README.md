# MkiiStepSequencer_JavaEdition
a much-needed re-design of my python step sequencer (here: https://github.com/sqrtM/arturia_mkii_step_sequencer), now in java.

the sequencer works by through bitwise operations which track the beat's location within the sequence as well as the notes which the user has marked as "active". 

the lack of unsigned types in java makes this needlessly painful; so I'm using some very strange workarounds (like the truly evil "using a char as an uint16") which are certainly not something i would want another developer to have to stomach working with. this is mostly just fun and practice working with primative data types in a weird way (which seemed appropriate, given the amount of "0x" in anything which communicates with hardware like this). 

i would also like—eventually—for this program to be fully generic. that meaning, once I get the GUI up and running and everything works and threads out the way i would like it to, I would like SysEx messages to be fully customizable by the end user so this could technically be used to turn any MIDI controller with lights on it into a step sequencer. i think that would be cool. 
