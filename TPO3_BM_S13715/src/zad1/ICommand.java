package zad1;

import java.io.Serializable;

public interface ICommand extends Serializable {

	public Object handle(Object... args) throws Exception;
}
