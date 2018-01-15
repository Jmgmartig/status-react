(ns status-im.utils.snoopy)


(defn status-module-filter [i]
  (or (= (.-module i) "Status")
      (= (.-method i) "<callback for Status.sendWeb3Request>")))

(defn timer-filter [i]
  (contains? #{"callTimers" "createTimer"} (.-method i)))

(defn filter-fn [i]
  true
  ;;RCTDeviceEventEmitter
  #_(or (= (.-module i) "Status")
      (= (.-method i) "<callback for Status.sendWeb3Request>"))
  #_(when (= (.-module i) "RCTDeviceEventEmitter")
      (println :arg (first (js->clj (.-args i)))))
  ;; "<callback for Status.sendWeb3Request>"
  #_(when (= (.-module i) "Status")
    (println :i (str (js->clj i))))
  (and (not (contains? #{"callTimers" "createTimer"} (.-method i)))
       (not (or (= (.-module i) "Status")
                (= (.-method i) "<callback for Status.sendWeb3Request>")))))

;; updateView

(defn subscribe! []
  (let [snoopy    (.-default (js/require "rn-snoopy"))
        sn-filter (.-default (js/require "rn-snoopy/stream/filter"))
        bars      (.-default (js/require "rn-snoopy/stream/bars"))
        buffer    (.-default (js/require "rn-snoopy/stream/buffer"))
        Emitter   (js/require "react-native/Libraries/vendor/emitter/EventEmitter")
        emitter   (Emitter.)
        events    (.stream snoopy emitter)]
    (.subscribe ((sn-filter (fn [i]
                              (and (not (timer-filter i))
                                   (not (status-module-filter i)))) true) events))
    ;; WebSocketModule


    #_(.subscribe ((bars
                  (fn [a] (.-length a))
                  200
                  false
                  " all")
                 ((buffer) events)))
    #_(.subscribe ((bars
                  (fn [a] (.-length a))
                  10
                  true
                  " module")
                 ((buffer) ((sn-filter status-module-filter false) events))))

    #_(.subscribe ((bars
                  (fn [a] (.-length a))
                  100
                  false
                  " timer")
                 ((buffer) ((sn-filter timer-filter false) events))))

    #_(.subscribe ((bars
                  (fn [a] (.-length a))
                  100
                  false
                  " not timer")
                 ((buffer) ((sn-filter (complement timer-filter) false) events))))))

