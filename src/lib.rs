pub mod events;
pub mod painter;

use egui::{Modifiers, Pos2, RawInput};

pub fn add(left: usize, right: usize) -> usize {
    left + right
}

pub struct EguiStateHandler {
    pub pointer_pos: Pos2,
    pub input: RawInput,
    pub modifiers: Modifiers,
    pub native_pixels_per_point: f32,
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        let result = add(2, 2);
        assert_eq!(result, 4);
    }
}
