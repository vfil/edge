(ns clj.new.edge-app-template
  (:require [clj.new.templates :refer [renderer project-name name-to-path ->files
                                       multi-segment sanitize-ns
                                       *force?*]]))

(def render (renderer "app.template"))

(defn edge-app-template
  "FIXME: write documentation"
  [name & opts]
  (let [opts (set (map #(keyword (subs % 2)) opts))
        data {:name (project-name name)
              :sanitized (name-to-path name)
              :root-ns (multi-segment (sanitize-ns name))
              :sass (contains? opts :sass)
              :cljs (contains? opts :cljs)
              :kick (or (contains? opts :sass)
                        (contains? opts :cljs))}]
    (println "Generating fresh 'clj new' edge.app-template project.")
    (->files data
             ["deps.edn" (render "deps.edn" data)]
             ["src/{{sanitized}}/foo.clj" (render "foo.clj" data)]
             ["src/config.edn" (render "config.edn" data)]
             ["dev/dev.clj" (render "dev.clj" data)])
    (binding [*force?* true]
      (when (:kick data)
        (->files data
                 ["src/index.html" (render "index.html" data)]
                 ["target/dev/.gitkeep" ""]
                 ["target/prod/.gitkeep" ""]))
      (if (:sass data)
        (->files data
                 ["src/{{name}}.scss" (render "app.scss" data)])
        (->files data
                 ["src/public/{{name}}.css" (render "app.css" data)]))
      (when (:cljs data)
        (->files data
                 ["src/{{sanitized}}/frontend/main.cljs" (render "main.cljs" data)]))
      )))
