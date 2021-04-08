import { NativeModules, Platform } from "react-native";

const { NotificationSounds } = NativeModules;

export default NotificationSounds;

export const playSampleSound = (soundObject, soundType = "unknown") => {
  const sound = Platform.OS === "ios" ? soundObject.soundID : soundObject.url;
  NotificationSounds.playSample(sound, soundType);
};

export const stopSampleSound = () => {
  NotificationSounds.stopSample();
};
