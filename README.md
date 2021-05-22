# doxa + re-frame study
![todo app main screen](/resources/screenshot1.png?raw=true "re-frame-doxa-study")

A barebone todo list app combining [doxa](https://github.com/ribelo/doxa) DB and re-frame.

Doxa is a new Clojure DB that works both on frontend and backend and strikes a good balance
between usability, features and speed.


[Youtube walkover video](https://youtu.be/t0gG1zzIT_c).


## Personal opinion
I've actually enjoyed working with doxa. This may be the easiest diff calc I've ever seen.
Doxa is still a fresh work so the API is expected to be a bit shaky.


## Clone and play

    git clone --recurse-submodules https://github.com/spacegangster/rf-doxa-etude

(if submodules won't work see this guide
  https://stackoverflow.com/questions/3796927/how-to-git-clone-including-submodules)


doxa is added as a submodule, and shadow-cljs expects it to be found in ./doxa

[malli-code-gen](https://github.com/dvingo/malli-code-gen) is added as a submodule, and shadow-cljs expects it to be found in ./malli-code-gen.


### malli and malli-code-gen
Malli is used for specs and malli-code-gen for generating an EQL pull vector from those specs.


## Credits
Kudos to [ribelo](https://github.com/ribelo)
for authoring doxa and being so fast, helpful and lovely.

Kudos to [Dan Vingo](http://github.com/dvingo) 
who has sparked the idea and sponsored the project.


## License
MIT