(ns hiccup2.page
  "Functions for setting up HTML pages."
  (:require [hiccup2.core :refer [html]] 
            [hiccup2.def :refer [defelem]]
            [hiccup.util :as util]))

(def doctype
  "Map of doctype strings."
  {:html4
   (util/raw-string "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" "
                    "\"http://www.w3.org/TR/html4/strict.dtd\">\n")
   :xhtml-strict
   (util/raw-string "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                    "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n")
   :xhtml-transitional
   (util/raw-string "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
                    "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n")
   :html5
   (util/raw-string "<!DOCTYPE html>\n")})

(defelem xhtml-tag
  "Create an XHTML element for the specified language."
  [lang & contents]
  [:html {:xmlns "http://www.w3.org/1999/xhtml"
          "xml:lang" lang
          :lang lang}
    contents])

(defn xml-declaration
  "Create a standard XML declaration for the following encoding."
  [encoding]
  (util/raw-string "<?xml version=\"1.0\" encoding=\"" encoding "\"?>\n"))

(defmacro html4
  "Create a HTML 4 document with the supplied contents. The first argument
  may be an optional attribute map."
  [& contents]
  `(html {:mode :sgml}
     (doctype :html4)
     [:html ~@contents]))

(defmacro xhtml
  "Create a XHTML 1.0 strict document with the supplied contents. The first
  argument may be an optional attribute map. The following attributes are
  treated specially:
  `:lang`
  : The language of the document
  `:encoding`
  : The character encoding of the document (defaults to UTF-8)."
  [options & contents]
  (if-not (map? options)
    `(xhtml {} ~options ~@contents)
    `(let [options# ~options]
       (html {:mode :xml}
         (xml-declaration (options# :encoding "UTF-8"))
         (doctype :xhtml-strict)
         (xhtml-tag (options# :lang) ~@contents)))))

(defmacro html5
  "Create a HTML5 document with the supplied contents."
  [options & contents]
  (if-not (map? options)
    `(html5 {} ~options ~@contents)
    (if (options :xml?)
      `(let [options# (dissoc ~options :xml?)]
         (html {:mode :xml}
           (xml-declaration (options# :encoding "UTF-8"))
           (doctype :html5)
           (xhtml-tag options# (options# :lang) ~@contents)))
      `(let [options# (dissoc ~options :xml?)]
         (html {:mode :html}
           (doctype :html5)
           [:html options# ~@contents])))))

(defn include-js
  "Include a list of external javascript files."
  [& scripts]
  (for [script scripts]
    [:script {:src (util/to-uri script)}]))

(defn include-css
  "Include a list of external stylesheet files."
  [& styles]
  (for [style styles]
    [:link {:type "text/css", :href (util/to-uri style), :rel "stylesheet"}]))
