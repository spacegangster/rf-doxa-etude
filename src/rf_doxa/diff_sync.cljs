(ns rf-doxa.diff-sync
  "Diff sync notes from ribelo's comment
  https://github.com/ribelo/doxa/issues/10"
  (:require
    [re-frame.core :as rf]
    [ribelo.doxa :as dx]))


(comment
  "diff generation is done with editscript"

  "1." ribelo.doxa/create-dx "takes the key with-diff? as an argument"

  "2. diff is generated during commits, and written to the db metadata in "
  ribelo.doxa/-commit ", see near the end"
  '(vary-meta assoc :tx (ese/get-edits (es/diff db db' {:algo :quick})))

  "3. Saving db/diff can be done with" ribelo.doxa/listen! " which takes"
  "a function that gets the db along with that metadata as argument.")


;
; In addition, if we would like to update db with generated diff, this is also already done. There is a patch and patch! function, that update db using editscript edits while preserving metadata.
;
; I currently use this to sync with firebase, where in addition to holding the entire db, I hold and watch diffs, which is much cheaper and basically costless.
;
; I'm going to document it somewhere, but the whole concept is still clarifying. The main reason for creating doxa was the frustration of synchronizing datascript with anything.
