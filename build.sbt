name := "vstore"

version := "0.1"

scalaVersion := "2.13.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka"                 %% "akka-http"                     % "10.1.9"
  , "com.typesafe.akka"               %% "akka-stream"                   % "2.5.23"
  , "eu.timepit"                      %% "refined"                       % "0.9.9"
//  , "eu.timepit"                      %% "refined-cats"                  % "0.9.9"
  , "eu.timepit"                      %% "refined-eval"                  % "0.9.9"
  , "eu.timepit"                      %% "refined-jsonpath"              % "0.9.9"
  , "eu.timepit"                      %% "refined-pureconfig"            % "0.9.9"
  , "eu.timepit"                      %% "refined-scalacheck"            % "0.9.9"
  , "eu.timepit"                      %% "refined-scalaz"                % "0.9.9"
  , "eu.timepit"                      %% "refined-scodec"                % "0.9.9"
  , "eu.timepit"                      %% "refined-scopt"                 % "0.9.9"
  , "com.chuusai"                     %% "shapeless"                     % "2.3.3"
)