                             Stixar Graph Library

The stixar graph library is a library of fundamental graph algorithms and data
structures which are lacking in Java.  The main component consists of the
treatment of graphs.  There are relatively simple interfaces and
implementations of directed and undirected graphs with a rich attribute
system.  The graph implementations can grow and shrink and are reasonably
dynamic.  On top of this base, a host of graph algorithms have been
implemented.  Most of the implemented algorithms are well researched both in
theory and practice for optimization, but also strive for ease of use. 

                         History and Current Status.

This library has grown out of the original author's need to implement various 
graph algorithms in support of other (regrettably not open source) projects. 
Many parts of the code are stable and well documented.  Some parts are
not yet stable or well documented, but these parts tend to address problems 
which are often peripheral or unnecessary for many applications.  I have 
unfortunately not yet split the project into stable and unstable portions 
to address this.  I encourage anyone interested to simply take a look at the
source to the parts they want to use.  Code that is not stable won't have 
lots of javadocs, and probably has lots of 'XXX' markers.  Code that is 
completely unusable will have lots of obviously holes (ie stubs).  Or just 
run the unit tests.  Tests that succeed are generally fairly exhaustive.

                                    Build

run ant.

                                   Install

There's no installation really, the result is a jar to put in you classpath.

Happy Hacking!
Scott Cotton

