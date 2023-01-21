use egui::{Modifiers, Pos2, RawInput};

pub struct EguiStateHandler {
    pub pointer_pos: Pos2,
    pub input: RawInput,
    pub modifiers: Modifiers,
    pub native_pixels_per_point: f32,
}
