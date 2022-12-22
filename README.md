# MkiiStepSequencer_JavaEdition
a much-needed re-design of my python step sequencer (here: https://github.com/sqrtM/arturia_mkii_step_sequencer), now in java.

the sequencer works by through bitwise operations which track the beat's location within the sequence as well as the notes which the user has marked as "active". 

the lack of unsigned types in java makes this needlessly painful; so I'm using some very strange workarounds (like the truly evil "using a char as an uint16") which are certainly not something i would want another developer to have to stomach working with. this is mostly just fun and practice working with primative data types in a weird way (which seemed appropriate, given the amount of "0x" in anything which communicates with hardware like this). 

i would also like—eventually—for this program to be fully generic. that meaning, once I get the GUI up and running and everything works and threads out the way i would like it to, I would like SysEx messages to be fully customizable by the end user so this could technically be used to turn any MIDI controller with lights on it into a step sequencer. i think that would be cool. 

for now i'm just focused on messages which are specific to the Mkii, since that's the only controller i own and the only one I've ever worked with. currently, my design includes a few things that may or may not violate OOP best principles (but I'm working on it):
1. different threads of beats. i'm not sure, strictly speaking, if these need to be capital-T Threads per-say, but I do want them to run completely independently of one another. 
2. MIDI out capabilities which separate pad signals from everything else. I think this program should, ideally, be used as a middle man; something which recieves incoming signals from your keyboard and "splits" them out into different directions and sends them to different programs. my image here is the idea of being able to play a solo on the keyboard while you schedule visuals with the pad sequencer and modify those visuals with the knobs.
3. a solid GUI which allows the user to easily change colors, tempos, sizes, and individual rhythms of the different banks. maybe one bank should be green and another blue? one which is 4 long and the other 7. maybe even a graphical interface which allows you to change them out in relation with one other polyrhythmic style.
